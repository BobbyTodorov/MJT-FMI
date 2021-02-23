package bg.sofia.uni.fmi.mjt.wish.list.command;

import bg.sofia.uni.fmi.mjt.wish.list.ui.Commands;
import bg.sofia.uni.fmi.mjt.wish.list.ui.Messages;
import bg.sofia.uni.fmi.mjt.wish.list.storage.Account;
import bg.sofia.uni.fmi.mjt.wish.list.storage.ServerStorage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CommandExecutorTest {
    private static ServerStorage storage;
    private static CommandExecutor cmdExecutor;
    private static SocketChannel testClientSocket;

    private static final String TEST_USERNAME = "bobi";
    private static final String TEST_USERNAME2 = "pesho";
    private static final Account TEST_ACCOUNT = new Account(TEST_USERNAME, "bobi");
    private static final Account TEST_ACCOUNT2 = new Account(TEST_USERNAME2, "123");
    private static final String WISH = "wish1";
    private static final String WISH2 = "wish2";

    @BeforeClass
    public static void setUp() {
        storage = mock(ServerStorage.class);
        cmdExecutor = new CommandExecutor(storage);
        testClientSocket = mock(SocketChannel.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteForClientWithNullCommand() {
        cmdExecutor.executeForClient(null, testClientSocket);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteForClientWithNullClient() {
        cmdExecutor.executeForClient(new Command("any", new String[]{}), null);
    }

    @Test
    public void testCommandWithUnknownCommand() {
        String[] wrongCommands = {"registerr", "log-in", "log-out", "postwish", "getwish", "dc"};
        List<String> actuals = new ArrayList<>();
        List<String> expecteds = new ArrayList<>();
        for (String wrongCommand : wrongCommands) {
            actuals.add(cmdExecutor.executeForClient(new Command(wrongCommand,
                new String[]{TEST_USERNAME, "bobi"}), testClientSocket));
            expecteds.add(Messages.MESSAGE_UNKNOWN_COMMAND);
        }

        assertEquals("invalid commands must be recognized successfully", expecteds, actuals);
    }

    @Test
    public void testCommandRegisterInvalidUsername() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(false);
        String testWrongUsername = "bobi!";
        String expected = String.format(Messages.MESSAGE_INVALID_USERNAME, testWrongUsername);
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_REGISTER,
                    new String[]{testWrongUsername, "bobi"}), testClientSocket);
        assertEquals("\"register\" with invalid username does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandRegisterExistingUsername() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(false);
        when(storage.isUsernameRegistered(TEST_USERNAME)).thenReturn(true);
        String expected = String.format(Messages.MESSAGE_USERNAME_ALREADY_TAKEN, TEST_USERNAME);
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_REGISTER,
                    new String[]{TEST_USERNAME, "bobi123"}), testClientSocket);
        assertEquals("\"register\" with already existing username does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandRegisterValidUsername() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(false);
        when(storage.isUsernameRegistered(TEST_ACCOUNT.username())).thenReturn(false);
        String expected = String.format(Messages.MESSAGE_USER_SUCCESSFULLY_REGISTERED, TEST_USERNAME);
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_REGISTER,
                    new String[]{TEST_ACCOUNT.username(), TEST_ACCOUNT.password()}), testClientSocket);
        verify(storage, times(1)).addRegisteredAccount(TEST_ACCOUNT);
        assertEquals("\"register\" with valid username does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandRegisterWhileLoggedIn() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(true);
        String expected = Messages.MESSAGE_PLEASE_LOGOUT;
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_REGISTER,
                    new String[]{TEST_USERNAME, "bob1"}), testClientSocket);
        assertEquals("\"register\" while logged-in does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandLogInWhileLoggedIn() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(true);
        String expected = Messages.MESSAGE_PLEASE_LOGOUT;
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_LOG_IN,
                    new String[]{TEST_USERNAME, "bob1"}), testClientSocket);
        assertEquals("\"login\" while logged-in does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandLogInWhileLoggedInFromThatClient() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(true);
        String expected = Messages.MESSAGE_PLEASE_LOGOUT;
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_LOG_IN,
                new String[]{TEST_USERNAME, "bob1"}), testClientSocket);
        assertEquals("\"login\" while logged-in does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandLogInWithAccountLoggedInFromAnotherClient() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(false);
        when(storage.getAccountByUsername(TEST_ACCOUNT.username())).thenReturn(TEST_ACCOUNT);
        when(storage.isAccountLoggedInFromAnyClient(TEST_ACCOUNT)).thenReturn(true);
        String expected = String.format(Messages.MESSAGE_USER_LOGGED_IN_FROM_ANOTHER_CLIENT, TEST_ACCOUNT.username());
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_LOG_IN,
                    new String[]{TEST_ACCOUNT.username(), TEST_ACCOUNT.password()}), testClientSocket);
        assertEquals("\"login\" while account is logged-in from another client does not work as supposed",
            expected, actual);
    }

    @Test
    public void testCommandLogInWithNotExistingUsername() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(false);
        when(storage.getAccountByUsername(TEST_ACCOUNT.username())).thenReturn(null);
        when(storage.isAccountLoggedInFromAnyClient(TEST_ACCOUNT)).thenReturn(false);
        String expected = String.format(Messages.MESSAGE_INVALID_USER, TEST_ACCOUNT.username());
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_LOG_IN,
            new String[]{TEST_ACCOUNT.username(), TEST_ACCOUNT.password()}), testClientSocket);
        assertEquals("\"login\" with not existing username does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandLogInWithWrongPassword() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(false);
        when(storage.getAccountByUsername(TEST_ACCOUNT.username())).thenReturn(TEST_ACCOUNT);
        when(storage.isAccountLoggedInFromAnyClient(TEST_ACCOUNT)).thenReturn(false);
        String expected = String.format(Messages.MESSAGE_INVALID_USER, TEST_ACCOUNT.username());
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_LOG_IN,
            new String[]{TEST_ACCOUNT.username(), TEST_ACCOUNT.password() + "abc"}), testClientSocket);
        assertEquals("\"login\" with wrong password client does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandLogInSuccess() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(false);
        when(storage.getAccountByUsername(TEST_ACCOUNT.username())).thenReturn(TEST_ACCOUNT);
        when(storage.isAccountLoggedInFromAnyClient(TEST_ACCOUNT)).thenReturn(false);
        String expected = String.format(Messages.MESSAGE_USER_LOGIN, TEST_ACCOUNT.username());
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_LOG_IN,
            new String[]{TEST_ACCOUNT.username(), TEST_ACCOUNT.password()}), testClientSocket);
        verify(storage, times(1)).addLoggedInAccountFromClient(TEST_ACCOUNT, testClientSocket);
        assertEquals("\"login\" does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandLogOutWhileLoggedOut() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(false);
        String expected = Messages.MESSAGE_NOT_LOGGED_IN;
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_LOG_OUT, new String[]{}),
            testClientSocket);
        assertEquals("\"logout\" while logged-out does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandLogOutWhileLoggedIn() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(true);
        String expected = Messages.MESSAGE_LOG_OUT;
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_LOG_OUT, new String[]{}),
            testClientSocket);
        assertEquals("\"logout\" while logged-in does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandPostWishWhileLoggedOut() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(false);
        String expected = Messages.MESSAGE_NOT_LOGGED_IN;
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_POST_WISH,
                new String[]{TEST_USERNAME, WISH}), testClientSocket);
        assertEquals("\"post-wish\" while logged-out does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandPostWishToNotExistingUsername() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(true);
        when(storage.getAccountByUsername(TEST_USERNAME)).thenReturn(null);
        String expected = String.format(Messages.MESSAGE_USER_NOT_REGISTERED, TEST_USERNAME);
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_POST_WISH,
                new String[]{TEST_USERNAME, WISH}), testClientSocket);
        assertEquals("\"post-wish\" to not existing username does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandPostWishWithUniqueWish() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(true);
        when(storage.getAccountByUsername(TEST_USERNAME)).thenReturn(TEST_ACCOUNT);
        when(storage.hasAccountWishList(TEST_ACCOUNT)).thenReturn(false);
        String expected = String.format(Messages.MESSAGE_GIFT_SUBMITTED_FOR_USER, WISH, TEST_USERNAME);
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_POST_WISH,
            new String[]{TEST_USERNAME, WISH}), testClientSocket);
        verify(storage, times(1)).addWishToAccount(TEST_ACCOUNT, WISH);
        assertEquals("\"post-wish\" with unique wish does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandPostWishWithSameWish() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(true);
        when(storage.getAccountByUsername(TEST_USERNAME)).thenReturn(TEST_ACCOUNT);
        when(storage.hasAccountWishList(TEST_ACCOUNT)).thenReturn(true);
        when(storage.getAccountWishList(TEST_ACCOUNT)).thenReturn(List.of(WISH));
        String expected = String.format(Messages.MESSAGE_SAME_GIFT_FOR_USER, TEST_USERNAME);
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_POST_WISH,
            new String[]{TEST_USERNAME, WISH}), testClientSocket);
        assertEquals("\"post-wish\" with same wish does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandGetWishWhileLoggedOut() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(false);
        String expected = Messages.MESSAGE_NOT_LOGGED_IN;
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_GET_WISH,
            new String[]{}), testClientSocket);
        assertEquals("\"get-wish\" while logged out does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandGetWishWithNoWishes() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(true);
        when(storage.hasNoWishLists()).thenReturn(true);
        String expected = Messages.MESSAGE_NO_MORE_PEOPLE;
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_GET_WISH,
            new String[]{}), testClientSocket);
        assertEquals("\"get-wish\" with no wishes does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandGetWishWithOneRegisteredAccount() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(true);
        when(storage.hasNoWishLists()).thenReturn(false);
        when(storage.wishListsCount()).thenReturn(1);
        when(storage.hasAccountWishList(TEST_ACCOUNT)).thenReturn(true);
        when(storage.getLoggedInAccountFromClient(testClientSocket)).thenReturn(TEST_ACCOUNT);
        String expected = Messages.MESSAGE_NO_MORE_PEOPLE;
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_GET_WISH,
            new String[]{}), testClientSocket);
        assertEquals("\"get-wish\" calling from the only registered account does not work as supposed",
            expected, actual);
    }

    @Test
    public void testCommandGetWishFromAnotherAccount() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(true);
        when(storage.hasNoWishLists()).thenReturn(false);
        when(storage.wishListsCount()).thenReturn(2);
        when(storage.getLoggedInAccountFromClient(testClientSocket)).thenReturn(TEST_ACCOUNT);
        when(storage.getAccountsWithWishList()).thenReturn(Set.of(TEST_ACCOUNT, TEST_ACCOUNT2));
        when(storage.getAccountWishList(TEST_ACCOUNT2)).thenReturn(List.of(WISH2));
        String expected = String.format(Messages.MESSAGE_USER_TO_WISH_LIST,
            TEST_ACCOUNT2.username(), " [" + WISH2 + "] ");
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_GET_WISH,
            new String[]{}), testClientSocket);
        verify(storage, times(1)).removeAccountFromWishListCol(TEST_ACCOUNT2);
        assertEquals("\"get-wish\" from another account does not work as supposed", expected, actual);
    }

    @Test
    public void testCommandGetWishFromAnotherAccountWithTwoWishes() {
        when(storage.isAnyoneLoggedInFromClient(testClientSocket)).thenReturn(true);
        when(storage.hasNoWishLists()).thenReturn(false);
        when(storage.wishListsCount()).thenReturn(2);
        when(storage.getLoggedInAccountFromClient(testClientSocket)).thenReturn(TEST_ACCOUNT);
        when(storage.getAccountsWithWishList()).thenReturn(Set.of(TEST_ACCOUNT, TEST_ACCOUNT2));
        when(storage.getAccountWishList(TEST_ACCOUNT2)).thenReturn(List.of(WISH, WISH2));
        String expected = String.format(Messages.MESSAGE_USER_TO_WISH_LIST,
            TEST_ACCOUNT2.username(), " [" + WISH + ", " + WISH2 + "] ");
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_GET_WISH,
            new String[]{}), testClientSocket);
        assertEquals("\"get-wish\" from another account with two wishes does not work as supposed",
            expected, actual);
    }

    @Test
    public void testCommandDisconnectSuccess() {
        String expected = Messages.MESSAGE_CLIENT_DISCONNECTED;
        String actual = cmdExecutor.executeForClient(new Command(Commands.COMMAND_DISCONNECT, new String[]{}),
            testClientSocket);
        verify(storage, times(1)).removeAccountLoggedInFromClient(testClientSocket);
        assertEquals("\"disconnect\" does not work as supposed", expected, actual);
    }
}
