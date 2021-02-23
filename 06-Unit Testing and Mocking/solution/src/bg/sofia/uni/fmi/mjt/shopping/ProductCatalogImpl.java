package bg.sofia.uni.fmi.mjt.shopping;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ProductCatalogImpl implements ProductCatalog {

    private Map<String, ProductInfo> productsInfo;

    public ProductCatalogImpl() {
        productsInfo = new LinkedHashMap<>();
    }

    @Override
    public ProductInfo getProductInfo(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null.");
        }

        if (!productsInfo.containsKey(id)) {
            throw new ItemNotFoundException("There is no item with such id in catalog.");
        }

        return productsInfo.get(id);
    }

    public void addProductInfo(String productId, ProductInfo newProductInfo) {
        if (productId == null) {
            throw new IllegalArgumentException("productId must not be null");
        }

        if (newProductInfo == null) {
            throw new IllegalArgumentException("newProductInfo must not be null.");
        }

        productsInfo.put(productId, newProductInfo);
    }

    public void removeProductInfo(String productId) {
        if (productId == null) {
            throw new IllegalArgumentException("productId must not be null");
        }

        if (!productsInfo.containsKey(productId)) {
            throw new ItemNotFoundException("There is no item with such id in catalog.");
        }

        productsInfo.remove(productId);
    }
}
