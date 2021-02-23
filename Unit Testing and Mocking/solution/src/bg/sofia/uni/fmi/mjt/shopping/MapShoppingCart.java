package bg.sofia.uni.fmi.mjt.shopping;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

public class MapShoppingCart implements ShoppingCart {

    public Map<Item, Integer> items;
    public ProductCatalog catalog;

    public MapShoppingCart(ProductCatalog catalog) {
        items = new LinkedHashMap<>();
        this.catalog = catalog;
    }

    public Map<Item, Integer> getItems() {
        return items;
    }

    public Collection<Item> getUniqueItems() {
        Collection<Item> i = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            i.add(entry.getKey());
        }
        return i;
    }

    @Override
    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null.");
        }

        if (items.containsKey(item)) {
            Integer i = items.get(item);
            items.put(item, i + 1);
        } else {
            items.put(item, 1);
        }
    }

    @Override
    public void removeItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null.");
        }

        if (!items.containsKey(item)) {
            throw new ItemNotFoundException("There is no such item in the cart.");
        }

        int occurrences = items.get(item) - 1;
        if (occurrences == 0) {
            items.remove(item);
        } else {
            items.put(item, occurrences);
        }
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            ProductInfo info = catalog.getProductInfo(entry.getKey().getId());
            total += info.price() * entry.getValue();
        }

        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        List<Item> sortedItems = new ArrayList<>(items.keySet());
        sortedItems.sort(new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return items.get(item1).compareTo(items.get(item2));
            }
        });
        return sortedItems;
    }

}
