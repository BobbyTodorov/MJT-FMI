package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Apple;
import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ListShoppingCartTest {

    @InjectMocks
    private ListShoppingCart listShoppingCart;


    @Test
    public void testGetItemsWithNoItemsInCart() {
        ArrayList<Item> actual = listShoppingCart.getItems();
        ArrayList<Item> expected = new ArrayList<>();

        assertEquals("GetItems with no items in cart failed.", expected, actual);
    }

    @Test
    public void testGetItemsGettingCorrectItems() {
        Item apple = new Apple("apple");
        Item chocolate = new Chocolate("chocolate");
        listShoppingCart.addItem(apple);
        listShoppingCart.addItem(chocolate);

        ArrayList<Item> expected = new ArrayList<>();
        expected.add(apple);
        expected.add(chocolate);

        assertEquals("GetItems getting correct items failed.", expected, listShoppingCart.getItems());
    }

    @Test
    public void testAddItemItemAddition() {
        Item apple = new Apple("apple");

        listShoppingCart.addItem(apple);

        ArrayList<Item> expected = new ArrayList<>();
        expected.add(apple);

        assertEquals("Item addition failed.", expected, listShoppingCart.getItems());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemInvalidArgument() {
        listShoppingCart.addItem(null);
    }

    @Test
    public void testGetUniqueItemsGettingCorrectItems() {
        Item apple = new Apple("apple");
        Item chocolate = new Chocolate("chocolate");
        listShoppingCart.addItem(apple);
        listShoppingCart.addItem(chocolate);
        listShoppingCart.addItem(apple);
        listShoppingCart.addItem(chocolate);
        Collection<Item> actual = listShoppingCart.getUniqueItems();

        Collection<Item> expected = new TreeSet<>(listShoppingCart.getItems());

        assertEquals("GetUniqueItems failed to return the correct items.", expected, actual);
    }

    @Test
    public void testGetUniqueItemsWithNoItemsInCart() {
        Collection<Item> actual = listShoppingCart.getUniqueItems();

        Collection<Item> expected = new TreeSet<>();

        assertEquals("GetUniqueItemsWithNoItemsInCart failed to return empty collection.", expected, actual);
    }

    @Test
    public void testRemoveItemRemovingCorrectItem() {
        Item apple = new Apple("apple");
        Item chocolate = new Chocolate("chocolate");
        Item apple2 = new Apple("apple2");

        listShoppingCart.addItem(apple);
        listShoppingCart.addItem(chocolate);
        listShoppingCart.addItem(apple2);

        listShoppingCart.removeItem(apple);

        ArrayList<Item> expected = new ArrayList<>();
        expected.add(chocolate);
        expected.add(apple2);

        assertEquals("RemovingCorrectItem failed to return correct ArrayList.", expected, listShoppingCart.getItems());
    }

    @Test(expected = ItemNotFoundException.class)
    public void testRemoveItemThatIsNotInTheCart() {
        Item apple = new Apple("apple");

        listShoppingCart.addItem(new Chocolate("chocolate"));

        listShoppingCart.removeItem(apple);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveItemNullArgument() {
        listShoppingCart.addItem(new Apple("apple"));

        listShoppingCart.removeItem(null);
    }

    @Test
    public void testGetTotalFromEmptyCart() {
        assertEquals("getTotal on empty cart must be 0.", 0.0, listShoppingCart.getTotal(), 0.01);
    }

    @Test
    public void testGetTotalFromItemsInCart() {
        Item apple = new Apple("apple");
        Item chocolate = new Chocolate("chocolate");
        ProductInfo appleInfo = new ProductInfo("apple", "yummy apple", 0.30);
        ProductInfo chocolateInfo = new ProductInfo("chocolate", "yummy chocolate", 1.80);
        ProductCatalogImpl productCatalog = new ProductCatalogImpl();
        productCatalog.addProductInfo(apple.getId(), appleInfo);
        productCatalog.addProductInfo(chocolate.getId(), chocolateInfo);

        ListShoppingCart listShoppingCart = new ListShoppingCart(productCatalog);
        listShoppingCart.addItem(apple);
        listShoppingCart.addItem(chocolate);

        double expected = appleInfo.price() + chocolateInfo.price();

        assertEquals("getTotal must work correctly.", expected, listShoppingCart.getTotal(), 0.01);
    }

    @Test
    public void testGetSortedItemsGettingCorrectlySortedItems() {
        Item apple = new Apple("apple");
        Item chocolate = new Chocolate("chocolate");
        Item apple2 = new Apple("apple2");

        listShoppingCart.addItem(apple);
        listShoppingCart.addItem(apple);
        listShoppingCart.addItem(chocolate);
        listShoppingCart.addItem(apple2);
        listShoppingCart.addItem(apple2);
        listShoppingCart.addItem(apple2);

        Collection<Item> expected = new ArrayList<>();
        expected.add(chocolate);
        expected.add(apple);
        expected.add(apple2);

        Collection<Item> actual = listShoppingCart.getSortedItems();

        assertEquals("GetSortedItems must return items sorted by quantity.", expected, actual);
    }

    @Test
    public void testGetSortedItemsEmptyCart() {
        Collection<Item> expected = new ArrayList<>();

        Collection<Item> actual = listShoppingCart.getSortedItems();

        assertEquals("GetSortedItems with Empty Cart must return [].", expected, actual);
    }
}
