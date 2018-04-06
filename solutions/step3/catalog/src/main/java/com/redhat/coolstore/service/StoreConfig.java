package com.redhat.coolstore.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties (prefix = "store")
public class StoreConfig {

    private List<String> recalledProducts;

    public List<String> getRecalledProducts() {
        return recalledProducts;
    }

    public void setRecalledProducts(List<String> recalledProducts) {
        this.recalledProducts = recalledProducts;
    }

    public boolean isRecalled(String itemId) {
        return recalledProducts.contains(itemId);
    }
}
