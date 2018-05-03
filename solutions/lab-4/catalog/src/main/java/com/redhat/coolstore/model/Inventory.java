package com.redhat.coolstore.model;

public class Inventory {
    private String itemId;
    private int quantity;
    
    public String getItemId() {
        return itemId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}