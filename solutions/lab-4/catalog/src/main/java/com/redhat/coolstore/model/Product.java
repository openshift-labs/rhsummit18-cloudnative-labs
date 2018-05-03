package com.redhat.coolstore.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product {
    @Id
    private String itemId;
    private String name;
    @Column(length = 2500)
    private String description;
    private double price;
    private int quantity;
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getItemId() {
        return itemId;
    }
    
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
}