package bg.sofia.uni.fmi.mjt.shopping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

public class ListShoppingCart implements ShoppingCart {

    private ArrayList<Item> items;
    private ProductCatalog catalog;

    public ListShoppingCart(ProductCatalog catalog) {
        this.catalog = catalog;
        this.items = new ArrayList<>();
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public ProductCatalog getCatalog() {
        return catalog;
    }

    @Override
    public Collection<Item> getUniqueItems() {
        return new TreeSet<>(items);
    }

    @Override
    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null.");
        }

        items.add(item);
    }

    @Override
    public void removeItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null.");
        }

        if (!items.contains(item)) {
            throw new ItemNotFoundException("There is no such item in the cart.");
        }

        items.remove(item);
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (Item item : items) {
            ProductInfo info = catalog.getProductInfo(item.getId());
            total += info.price();
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        Map<Item, Integer> itemToQuantity = create_map();
        Map<Item, Integer> sortedItems = new TreeMap<>(new Comparator<Item>() {
            public int compare(Item item1, Item item2) {
                return itemToQuantity.get(item1).compareTo(itemToQuantity.get(item2));
            }
        });
        sortedItems.putAll(itemToQuantity);
        return new ArrayList<>(sortedItems.keySet());
    }

    private Map<Item, Integer> create_map() {
        HashMap<Item, Integer> itemToQuantity = new HashMap<Item, Integer>();
        for (Item item : items) {
            boolean condition = itemToQuantity.containsKey(item);
            itemToQuantity.put(item, condition ? itemToQuantity.get(item) + 1 : 1);
        }
        return itemToQuantity;
    }
}

