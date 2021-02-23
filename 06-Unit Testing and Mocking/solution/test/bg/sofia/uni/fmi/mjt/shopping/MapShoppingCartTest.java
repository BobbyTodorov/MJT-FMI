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
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MapShoppingCartTest {

    @InjectMocks
    private MapShoppingCart mapShoppingCart;


    @Test
    public void testGetItemsWithNoItemsInCart() {
        Map<Item, Integer> actual = mapShoppingCart.getItems();
        Map<Item, Integer> expected = new LinkedHashMap<>();

        assertEquals("GetItems with no items in cart failed.", expected, actual);
    }

    @Test
    public void testGetItemsGettingCorrectItems() {
        Item apple = new Apple("apple");
        Item chocolate = new Chocolate("chocolate");
        mapShoppingCart.addItem(apple);
        mapShoppingCart.addItem(apple);
        mapShoppingCart.addItem(chocolate);

        Map<Item, Integer> expected = new LinkedHashMap<>();
        expected.put(apple, 2);
        expected.put(chocolate, 1);

        assertEquals("GetItems getting correct items failed.", expected, mapShoppingCart.getItems());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemInvalidArgument() {
        mapShoppingCart.addItem(null);
    }

    @Test
    public void testAddItemItemAddition() {
        Item apple = new Apple("apple");

        mapShoppingCart.addItem(apple);

        Map<Item, Integer> expected = new LinkedHashMap<>();
        expected.put(apple, 1);

        assertEquals("Item addition failed.", expected, mapShoppingCart.getItems());
    }

    @Test
    public void testGetUniqueItemsGettingCorrectItems() {
        Item apple = new Apple("apple");
        Item chocolate = new Chocolate("chocolate");
        mapShoppingCart.addItem(apple);
        mapShoppingCart.addItem(chocolate);
        mapShoppingCart.addItem(apple);
        mapShoppingCart.addItem(chocolate);
        Collection<Item> actual = mapShoppingCart.getUniqueItems();

        Collection<Item> expected = new ArrayList<>();
        expected.add(apple);
        expected.add(chocolate);

        assertEquals("GetUniqueItems failed to return the correct items.", expected, actual);
    }

    @Test
    public void testGetUniqueItemsWithNoItemsInCart() {
        Collection<Item> actual = mapShoppingCart.getUniqueItems();

        Collection<Item> expected = new ArrayList<>();

        assertEquals("GetUniqueItemsWithNoItemsInCart failed to return empty collection.", expected, actual);
    }

    @Test(expected = ItemNotFoundException.class)
    public void testRemoveItemThatIsNotInTheCart() {
        Item apple = new Apple("apple");

        mapShoppingCart.addItem(new Chocolate("chocolate"));

        mapShoppingCart.removeItem(apple);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveItemNullArgument() {
        mapShoppingCart.addItem(new Apple("apple"));

        mapShoppingCart.removeItem(null);
    }

    @Test
    public void testRemoveItemRemovingCorrectItem() {
        Item apple = new Apple("apple");
        Item chocolate = new Chocolate("chocolate");

        mapShoppingCart.addItem(apple);
        mapShoppingCart.addItem(chocolate);
        mapShoppingCart.addItem(apple);
        mapShoppingCart.addItem(apple);

        mapShoppingCart.removeItem(apple);

        Map<Item, Integer> expected = new LinkedHashMap<>();
        expected.put(apple, 2);
        expected.put(chocolate, 1);

        assertEquals("RemovingCorrectItem failed to return correct items.", expected, mapShoppingCart.getItems());
    }

    @Test
    public void testRemoveItemWhenAnItemIsLastOfAKind() {
        Item apple = new Apple("apple");
        Item chocolate = new Chocolate("chocolate");

        mapShoppingCart.addItem(chocolate);
        mapShoppingCart.addItem(apple);

        mapShoppingCart.removeItem(chocolate);

        Map<Item, Integer> expected = new LinkedHashMap<>();
        expected.put(apple, 1);

        assertEquals("RemovingLastOfAKindItem failed to return correct items.", expected, mapShoppingCart.getItems());
    }

    @Test
    public void testGetTotalFromEmptyCart() {
        assertEquals("getTotal on empty cart must be 0.", 0.0, mapShoppingCart.getTotal(), 0.01);
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

        MapShoppingCart mapShoppingCart = new MapShoppingCart(productCatalog);
        mapShoppingCart.addItem(apple);
        mapShoppingCart.addItem(chocolate);

        double expected = appleInfo.price() + chocolateInfo.price();

        assertEquals("getTotal must work correctly.", expected, mapShoppingCart.getTotal(), 0.01);
    }

    @Test
    public void testGetTotalCartWithSameKindItems() {
        Item apple = new Apple("apple");
        ProductInfo appleInfo = new ProductInfo("apple", "yummy apple", 0.30);
        ProductCatalogImpl productCatalog = new ProductCatalogImpl();
        productCatalog.addProductInfo(apple.getId(), appleInfo);

        MapShoppingCart mapShoppingCart = new MapShoppingCart(productCatalog);
        mapShoppingCart.addItem(apple);
        mapShoppingCart.addItem(apple);

        double expected = appleInfo.price() * 2.0;

        assertEquals("getTotal must work correctly.", expected, mapShoppingCart.getTotal(), 0.01);
    }

    @Test
    public void testGetSortedItemsGettingCorrectlySortedItems() {
        Item apple = new Apple("apple");
        Item chocolate = new Chocolate("chocolate");
        Item apple2 = new Apple("apple2");

        mapShoppingCart.addItem(apple);
        mapShoppingCart.addItem(apple);
        mapShoppingCart.addItem(chocolate);
        mapShoppingCart.addItem(apple2);
        mapShoppingCart.addItem(apple2);
        mapShoppingCart.addItem(apple2);

        Collection<Item> expected = new ArrayList<>();
        expected.add(chocolate);
        expected.add(apple);
        expected.add(apple2);

        Collection<Item> actual = mapShoppingCart.getSortedItems();

        assertEquals("GetSortedItems must return sorted by quantity.", expected, actual);
    }

    @Test
    public void testGetSortedItemsEmptyCart() {
        Collection<Item> expected = new ArrayList<>();

        Collection<Item> actual = mapShoppingCart.getSortedItems();

        assertEquals("GetSortedItems with Empty Cart must return [].", expected, actual);
    }
}