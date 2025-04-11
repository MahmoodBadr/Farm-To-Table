package com.example.farm_to_table;

public class Product {
    private String name;
    private String description;
    private double price;
    private int imageResourceId;
    private String farmName;

    public Product(String name, String description, double price, int imageResourceId, String farmName) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResourceId = imageResourceId;
        this.farmName = farmName;
    }

    public String getName() {
        return name;
    }
    public String getFarmName() {
        return farmName;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}