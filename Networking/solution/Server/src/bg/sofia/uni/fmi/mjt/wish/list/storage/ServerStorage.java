package bg.sofia.uni.fmi.mjt.wish.list.storage;

import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class ServerStorage {
    // In-memory server database and API.
    private final Map<Account, List<String>> accountToWishList;
    private final Map<SocketChannel, Account> loggedInAccounts;
    private final Set<Account> registeredAccounts;

    public ServerStorage() {
        accountToWishList = Collections.synchronizedMap(new LinkedHashMap<>());
        loggedInAccounts = Collections.synchronizedMap(new LinkedHashMap<>());
        registeredAccounts = Collections.synchronizedSet(new LinkedHashSet<>());
    }

    public boolean isAnyoneLoggedInFromClient(SocketChannel clientSocketChannel) {
        if (clientSocketChannel == null) {
            throw new IllegalArgumentException("clientSocketChannel must not be null");
        }
        return loggedInAccounts.containsKey(clientSocketChannel);
    }

    public boolean isAccountLoggedInFromAnyClient(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account must not be null");
        }
        return loggedInAccounts.containsValue(account);
    }

    public Account getAccountByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username must not be null");
        }
        AtomicReference<Account> result = new AtomicReference<>();
        registeredAccounts.forEach(a -> {
            if (username.equals(a.username())) {
                result.set(a);
            }
        });
        return result.get();
    }

    public void addLoggedInAccountFromClient(Account account, SocketChannel accountClientSocketChannel) {
        if (account == null || accountClientSocketChannel == null) {
            throw new IllegalArgumentException("addLoggedInAccountFromClient does not take null arguments");
        }
        loggedInAccounts.put(accountClientSocketChannel, account);
    }

    public void removeAccountLoggedInFromClient(SocketChannel clientSocketChannel) {
        if (clientSocketChannel == null) {
            throw new IllegalArgumentException("clientSocketChannel must not be null");
        }
        loggedInAccounts.remove(clientSocketChannel);
    }

    public Account getLoggedInAccountFromClient(SocketChannel clientSocketChannel) {
        if (clientSocketChannel == null) {
            throw new IllegalArgumentException("clientSocketChannel must not be null");
        }
        return loggedInAccounts.get(clientSocketChannel);
    }

    public void addRegisteredAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account must not be null");
        }
        registeredAccounts.add(account);
    }

    public boolean isUsernameRegistered(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username must not be null");
        }
        for (Account registeredAccount : registeredAccounts) {
            if (registeredAccount.username().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccountWishList(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account must not be null");
        }
        return accountToWishList.containsKey(account);
    }

    public Set<Account> getAccountsWithWishList() {
        return accountToWishList.keySet();
    }

    public List<String> getAccountWishList(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account must not be null");
        }
        return accountToWishList.get(account);
    }

    public void addWishToAccount(Account account, String wish) {
        if (account == null || wish == null) {
            throw new IllegalArgumentException("addWishToAccount does not take null arguments");
        }
        if (!accountToWishList.containsKey(account)) {
            accountToWishList.put(account, new LinkedList<>());
        }
        accountToWishList.get(account).add(wish);
    }

    public boolean hasNoWishLists() {
        return accountToWishList.isEmpty();
    }

    public int wishListsCount() {
        return accountToWishList.size();
    }

    public void removeAccountFromWishListCol(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account must not be null");
        }
        accountToWishList.remove(account);
    }

    public void clearStorage() { //test purposes
        loggedInAccounts.clear();
        registeredAccounts.clear();
        accountToWishList.clear();
    }
}
