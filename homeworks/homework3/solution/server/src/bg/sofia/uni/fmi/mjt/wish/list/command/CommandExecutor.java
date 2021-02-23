package bg.sofia.uni.fmi.mjt.wish.list.command;

import bg.sofia.uni.fmi.mjt.wish.list.storage.Account;
import bg.sofia.uni.fmi.mjt.wish.list.storage.ServerStorage;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static bg.sofia.uni.fmi.mjt.wish.list.ui.Commands.COMMAND_DISCONNECT;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Commands.COMMAND_GET_WISH;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Commands.COMMAND_LOG_IN;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Commands.COMMAND_LOG_OUT;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Commands.COMMAND_POST_WISH;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Commands.COMMAND_REGISTER;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_CLIENT_DISCONNECTED;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_GIFT_SUBMITTED_FOR_USER;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_INVALID_SYNTAX;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_INVALID_USER;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_INVALID_USERNAME;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_LOG_OUT;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_NOT_LOGGED_IN;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_NO_MORE_PEOPLE;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_PLEASE_LOGOUT;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_SAME_GIFT_FOR_USER;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_UNKNOWN_COMMAND;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_USERNAME_ALREADY_TAKEN;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_USER_LOGGED_IN_FROM_ANOTHER_CLIENT;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_USER_LOGIN;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_USER_NOT_REGISTERED;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_USER_SUCCESSFULLY_REGISTERED;
import static bg.sofia.uni.fmi.mjt.wish.list.ui.Messages.MESSAGE_USER_TO_WISH_LIST;

public class CommandExecutor {

    private final ServerStorage storage; //storage to execute commands over

    public CommandExecutor(ServerStorage storage) {
        this.storage = storage;
    }

    public String executeForClient(Command command, SocketChannel client) {
        if (command == null || client == null) {
            throw new IllegalArgumentException("executeForClient does not take null argument");
        }
        return switch (command.command()) {
            case COMMAND_LOG_IN -> commandLogIn(client, command.arguments());
            case COMMAND_LOG_OUT -> commandLogOut(client);
            case COMMAND_REGISTER -> commandRegister(client, command.arguments());
            case COMMAND_POST_WISH -> commandPostWish(client, command.arguments());
            case COMMAND_GET_WISH -> commandGetWish(client);
            case COMMAND_DISCONNECT -> commandDisconnect(client);
            default -> MESSAGE_UNKNOWN_COMMAND;
        };
    }

    private String commandLogIn(SocketChannel clientSocketChannel, String[] args) {
        if (args.length < 2) {
            return String.format(MESSAGE_INVALID_SYNTAX, COMMAND_LOG_IN);
        }
        if (storage.isAnyoneLoggedInFromClient(clientSocketChannel)) {
            return MESSAGE_PLEASE_LOGOUT;
        }

        String username = args[0];
        String password = args[1];
        Account accountToLogIn = storage.getAccountByUsername(username);
        if (storage.isAccountLoggedInFromAnyClient(accountToLogIn)) {
            return String.format(MESSAGE_USER_LOGGED_IN_FROM_ANOTHER_CLIENT, username);
        }
        if (accountToLogIn == null || !accountToLogIn.password().equals(password)) {
            return MESSAGE_INVALID_USER;
        }

        storage.addLoggedInAccountFromClient(accountToLogIn, clientSocketChannel);
        return String.format(MESSAGE_USER_LOGIN, username);
    }

    private String commandLogOut(SocketChannel clientSocketChannel) {
        if (!storage.isAnyoneLoggedInFromClient(clientSocketChannel)) {
            return MESSAGE_NOT_LOGGED_IN;
        }

        storage.removeAccountLoggedInFromClient(clientSocketChannel);
        return MESSAGE_LOG_OUT;
    }

    private String commandRegister(SocketChannel clientSocketChannel, String[] args) {
        if (args.length < 2) {
            return String.format(MESSAGE_INVALID_SYNTAX, COMMAND_REGISTER);
        }
        if (storage.isAnyoneLoggedInFromClient(clientSocketChannel)) {
            return MESSAGE_PLEASE_LOGOUT;
        }
        Account accountToRegister;
        String username = args[0];
        try {
            accountToRegister = new Account(username, args[1]);
        } catch (IllegalArgumentException e) {
            return String.format(MESSAGE_INVALID_USERNAME, username);
        }
        if (storage.isUsernameRegistered(username)) {
            return String.format(MESSAGE_USERNAME_ALREADY_TAKEN, username);
        }
        storage.addRegisteredAccount(accountToRegister);
        storage.addLoggedInAccountFromClient(accountToRegister, clientSocketChannel);
        return String.format(MESSAGE_USER_SUCCESSFULLY_REGISTERED, username);
    }

    private String commandPostWish(SocketChannel clientSocketChannel, String[] args) {
        if (args.length < 2) {
            return String.format(MESSAGE_INVALID_SYNTAX, COMMAND_POST_WISH);
        }
        if (!storage.isAnyoneLoggedInFromClient(clientSocketChannel)) {
            return MESSAGE_NOT_LOGGED_IN;
        }
        String username = args[0];
        Account account = storage.getAccountByUsername(username);
        if (account == null) {
            return String.format(MESSAGE_USER_NOT_REGISTERED, username);
        }
        String wish = getWishString(args);
        if (storage.hasAccountWishList(account) && storage.getAccountWishList(account).contains(wish)) {
            return String.format(MESSAGE_SAME_GIFT_FOR_USER, username);
        } else {
            storage.addWishToAccount(account, wish);
            return String.format(MESSAGE_GIFT_SUBMITTED_FOR_USER, wish, username);
        }
    }

    private String getWishString(String[] args) {
        StringBuilder wishString = new StringBuilder();
        int argsLength = args.length;
        int appendSpaceUntillIndex = argsLength - 1;
        for (int i = 1; i < argsLength; ++i) {
            wishString.append(args[i]);
            if (i < appendSpaceUntillIndex) {
                wishString.append(" ");
            }
        }
        return wishString.toString();
    }

    private String commandGetWish(SocketChannel clientSocketChannel) {
        if (!storage.isAnyoneLoggedInFromClient(clientSocketChannel)) {
            return MESSAGE_NOT_LOGGED_IN;
        }
        if (storage.hasNoWishLists()
            || (storage.wishListsCount() == 1
                && storage.hasAccountWishList(storage.getLoggedInAccountFromClient(clientSocketChannel)))) {
            return MESSAGE_NO_MORE_PEOPLE;
        } else {
            Account randomAccount = getRandomAccountExcept(storage.getLoggedInAccountFromClient(clientSocketChannel));
            if (randomAccount == null) {
                return MESSAGE_NO_MORE_PEOPLE;
            }
            String wishListString = getWishListStringByAccount(randomAccount);
            storage.removeAccountFromWishListCol(randomAccount);

            return String.format(MESSAGE_USER_TO_WISH_LIST, randomAccount.username(), wishListString);
        }
    }

    private Account getRandomAccountExcept(Account account) {
        if (storage.hasNoWishLists()) {
            return null;
        }
        Account randomAccount;
        List<Account> accountsList = new ArrayList<>(storage.getAccountsWithWishList());
        do {
            int randomIndex = new Random().nextInt(accountsList.size());
            randomAccount = accountsList.get(randomIndex);
        } while (randomAccount.equals(account));
        return randomAccount;
    }

    private String getWishListStringByAccount(Account account) {
        StringBuilder wishListString = new StringBuilder(" [");
        List<String> accountWishList = storage.getAccountWishList(account);
        for (String wishItem : accountWishList) {
            if (wishItem != null) {
                wishListString.append(wishItem);
                wishListString.append(", ");
            }
        }

        wishListString = new StringBuilder(wishListString.substring(0, wishListString.length() - 2)); //cut last comma
        return wishListString + "] ";
    }

    private String commandDisconnect(SocketChannel socketChannel) {
        storage.removeAccountLoggedInFromClient(socketChannel);
        return MESSAGE_CLIENT_DISCONNECTED;
    }
}
