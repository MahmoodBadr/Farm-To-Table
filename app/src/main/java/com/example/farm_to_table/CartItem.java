package com.example.farm_to_table;

public class CartItem {
    private String productName;
    private double price;
    private int quantity;
    private int imageResourceId;
    private String farmName;

    public CartItem(String productName, double price, int quantity, int imageResourceId, String farmName) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.imageResourceId = imageResourceId;
        this.farmName = farmName;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getFarmName() {
        return farmName;
    }

    public double getTotalPrice() {
        return price * quantity;
    }
}