package bg.sofia.uni.fmi.mjt.wish.list.storage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ServerStorageTest {
    private static ServerStorage storage;

    private static final String TEST_USERNAME = "bobi";
    private static final String TEST_USERNAME2 = "pesho";
    private static final Account TEST_ACCOUNT = new Account(TEST_USERNAME, "123");
    private static final Account TEST_ACCOUNT2 = new Account(TEST_USERNAME2, "123");
    private static final String TEST_WISH = "wish";
    private static final String TEST_WISH2 = "wish2";
    private static SocketChannel TEST_SC_CLIENT1;
    private static SocketChannel TEST_SC_CLIENT2;

    @BeforeClass
    public static void setup() throws IOException {
        storage = new ServerStorage();
        TEST_SC_CLIENT1 = SocketChannel.open();
        TEST_SC_CLIENT2 = SocketChannel.open();
    }

    @Before
    public void clear() {
        storage.clearStorage();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsAnyoneLoggedInFromClientWithNullArgument() {
        storage.isAnyoneLoggedInFromClient(null);
    }

    @Test
    public void testIsAnyoneLoggedInFromClientWithSomeoneLoggedInFromThatClient() {
        storage.addLoggedInAccountFromClient(TEST_ACCOUNT, TEST_SC_CLIENT1);
        assertTrue("isAnyoneLoggedInFromClient should return true",
            storage.isAnyoneLoggedInFromClient(TEST_SC_CLIENT1));
    }

    @Test
    public void testIsAnyoneLoggedInFromClientWithNoOneLoggedInFromThatClient() {
        storage.addLoggedInAccountFromClient(TEST_ACCOUNT, TEST_SC_CLIENT1);
        assertFalse("isAnyoneLoggedInFromClient should return false",
            storage.isAnyoneLoggedInFromClient(TEST_SC_CLIENT2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsAccountLoggedInFromAnyClientWithNullArgument() {
        storage.isAccountLoggedInFromAnyClient(null);
    }

    @Test
    public void testIsAccountLoggedInFromAnyClientWithLoggedInAccount() {
        storage.addLoggedInAccountFromClient(TEST_ACCOUNT, TEST_SC_CLIENT1);
        assertTrue("isAccountLoggedInFromAnyClient should return true",
            storage.isAccountLoggedInFromAnyClient(TEST_ACCOUNT));
    }

    @Test
    public void testIsAccountLoggedInFromAnyClientWithLoggedOutAccount() {
        assertFalse("isAccountLoggedInFromAnyClient should return false",
            storage.isAccountLoggedInFromAnyClient(TEST_ACCOUNT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAccountByUsernameWithNullArgument() {
        storage.getAccountByUsername(null);
    }

    @Test
    public void testGetAccountByUsernameWithExistingAccountWithThisUsername() {
        storage.addRegisteredAccount(TEST_ACCOUNT);
        assertEquals("getAccountByUsername should return correct username", TEST_ACCOUNT,
            storage.getAccountByUsername(TEST_USERNAME));
    }

    @Test
    public void testGetAccountByUsernameWithNoSuchAccount() {
        storage.addRegisteredAccount(TEST_ACCOUNT);
        assertNull("getAccountByUsername should return null", storage.getAccountByUsername(TEST_USERNAME2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddLoggedInAccountFromClientWithNullAccount() {
        storage.addLoggedInAccountFromClient(null, TEST_SC_CLIENT1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddLoggedInAccountFromClientWithNullSocketChannel() {
        storage.addLoggedInAccountFromClient(TEST_ACCOUNT, null);
    }

    @Test
    public void testAddLoggedInAccountFromClient() {
        storage.addLoggedInAccountFromClient(TEST_ACCOUNT, TEST_SC_CLIENT1);
        assertEquals("addLoggedInAccountFromClient must add account-client to storage", TEST_ACCOUNT,
            storage.getLoggedInAccountFromClient(TEST_SC_CLIENT1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveAccountLoggedInFromClientWithNullArgument() {
        storage.removeAccountLoggedInFromClient(null);
    }

    @Test
    public void testRemoveAccountLoggedInFromClientSuccess() {
        storage.addLoggedInAccountFromClient(TEST_ACCOUNT, TEST_SC_CLIENT1);
        storage.removeAccountLoggedInFromClient(TEST_SC_CLIENT1);
        assertNull("removeAccountLoggedInFromClient must remove the logged-in account to that client",
            storage.getLoggedInAccountFromClient(TEST_SC_CLIENT1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLoggedInAccountFromClientWithNullArgument() {
        storage.getLoggedInAccountFromClient(null);
    }

    @Test
    public void testGetLoggedInAccountFromClientSuccess() {
        storage.addLoggedInAccountFromClient(TEST_ACCOUNT, TEST_SC_CLIENT1);
        assertEquals("getLoggedInAccountFromClient must return the logged-in account for a client", TEST_ACCOUNT,
            storage.getLoggedInAccountFromClient(TEST_SC_CLIENT1));
    }

    @Test
    public void testGetLoggedInAccountFromClientWithNoLoggedInUserFromThatClient() {
        assertNull("getLoggedInAccountFromClient with no logged-in user from that client must return null",
            storage.getLoggedInAccountFromClient(TEST_SC_CLIENT1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddRegisteredAccountWithNullArgument() {
        storage.addRegisteredAccount(null);
    }

    @Test
    public void testAddRegisteredAccountSuccess() {
        storage.addRegisteredAccount(TEST_ACCOUNT);
        assertTrue("addRegisteredAccount must add the account to the registered ones",
            storage.isUsernameRegistered(TEST_USERNAME));
    }

    @Test
    public void testAddRegisteredAccountWithNoRegisteredAccounts() {
        assertFalse("addRegisteredAccount with no registered accounts must return false",
            storage.isUsernameRegistered(TEST_USERNAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsUsernameRegisteredWithNullArgument() {
        storage.isUsernameRegistered(null);
    }

    @Test
    public void testIsUsernameRegisteredWithRegisteredUsername() {
        storage.addRegisteredAccount(TEST_ACCOUNT);
        assertTrue("isUsernameRegistered with registered username must return true",
            storage.isUsernameRegistered(TEST_USERNAME));
    }

    @Test
    public void testIsUsernameRegisteredWithNotRegisteredUsername() {
        assertFalse("isUsernameRegistered with not registered username must return false",
            storage.isUsernameRegistered(TEST_USERNAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasAccountWishListWithNullArgument() {
        storage.hasAccountWishList(null);
    }

    @Test
    public void testHasAccountWishListWithAccountThatHasNoWishList() {
        assertFalse("hasAccountWishList wish account that has no wish list must return false",
            storage.hasAccountWishList(TEST_ACCOUNT));
    }

    @Test
    public void testHasAccountWishListWithAccountThatHasWishList() {
        storage.addWishToAccount(TEST_ACCOUNT, TEST_WISH);
        assertTrue("hasAccountWishList wish account that has a wish list must return true",
            storage.hasAccountWishList(TEST_ACCOUNT));
    }

    @Test
    public void testGetAccountsWithWishListSuccess() {
        storage.addWishToAccount(TEST_ACCOUNT, TEST_WISH);
        storage.addWishToAccount(TEST_ACCOUNT2, TEST_WISH2);
        storage.removeAccountFromWishListCol(TEST_ACCOUNT);
        assertEquals("getAccountsWithWishList must return set of accounts with wish list", Set.of(TEST_ACCOUNT2),
            storage.getAccountsWithWishList());
    }

    @Test
    public void testGetAccountsWithWishListWithNoAccountsWithWishList() {
        assertEquals("getAccountsWithWishList with no accounts with wish list must return empty set", Set.of(),
            storage.getAccountsWithWishList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAccountWishListWithNullArgument() {
        storage.getAccountWishList(null);
    }

    @Test
    public void testGetAccountWishListSuccess() {
        storage.addWishToAccount(TEST_ACCOUNT, TEST_WISH);
        assertEquals("getAccountWishList must return given account's wish list", List.of(TEST_WISH),
            storage.getAccountWishList(TEST_ACCOUNT));
    }

    @Test
    public void testGetAccountWishListWithAccountWithoutWishList() {
        assertNull("getAccountWishList wish account without wish list must return null\"",
            storage.getAccountWishList(TEST_ACCOUNT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddWishToAccountWithNullAccount() {
        storage.addWishToAccount(null, TEST_WISH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddWishToAccountWithNullWish() {
        storage.addWishToAccount(TEST_ACCOUNT, null);
    }

    @Test
    public void testAddWishToAccountFirstWish() {
        storage.addWishToAccount(TEST_ACCOUNT, TEST_WISH);
        assertEquals("testAddWishToAccount adding first wish must create account key and add wish as value",
            List.of(TEST_WISH), storage.getAccountWishList(TEST_ACCOUNT));
    }

    @Test
    public void testAddWishToAccountTwoWishes() {
        storage.addWishToAccount(TEST_ACCOUNT, TEST_WISH);
        storage.addWishToAccount(TEST_ACCOUNT, TEST_WISH2);
        assertEquals("testAddWishToAccount with second wish must add the wish to the account's value",
            List.of(TEST_WISH, TEST_WISH2), storage.getAccountWishList(TEST_ACCOUNT));
    }

    @Test
    public void testHasNoWishListsSuccess() {
        assertTrue("hasNoWishLists must return false if there are no posted wishes", storage.hasNoWishLists());
        storage.addWishToAccount(TEST_ACCOUNT, TEST_WISH);
        assertFalse("hasNoWishLists must return true if there are posted wishes", storage.hasNoWishLists());
    }

    @Test
    public void testWishListsCountSuccess() {
        assertEquals("wishListCount must return 0 if there are no wish lists", 0, storage.wishListsCount());
        storage.addWishToAccount(TEST_ACCOUNT, TEST_WISH);
        storage.addWishToAccount(TEST_ACCOUNT2, TEST_WISH);
        assertEquals("wishListCount must return correct number of wish lists", 2, storage.wishListsCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveAccountFromWishListColWithNullArgument() {
        storage.removeAccountFromWishListCol(null);
    }

    @Test
    public void testRemoveAccountFromWishListColSuccess() {
        storage.removeAccountFromWishListCol(TEST_ACCOUNT2);
        storage.addWishToAccount(TEST_ACCOUNT, TEST_WISH);
        storage.removeAccountFromWishListCol(TEST_ACCOUNT);
        assertTrue("removeAccountFromListCol must remove the account key from collection", storage.hasNoWishLists());
    }
}
