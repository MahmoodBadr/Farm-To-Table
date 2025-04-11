package com.example.farm_to_table;

import java.util.ArrayList;
import java.util.List;

public class ActiveOrder {
    public static boolean isOrderActive = false;
    private static ActiveOrder instance;
    private List<CartItem> activeOrderItems;

    // Private constructor to prevent instantiation
    private ActiveOrder() {
        activeOrderItems = new ArrayList<>();
    }

    // Get the instance of ActiveOrder (singleton)
    public static ActiveOrder getInstance() {
        if (instance == null) {
            instance = new ActiveOrder();
        }
        return instance;
    }

    // Method to copy the cart contents to ActiveOrder
    public void copyFromCart(List<CartItem> cartItems) {
        activeOrderItems.clear();
        activeOrderItems.addAll(cartItems);
    }

    public List<CartItem> getActiveOrderItems() {
        return activeOrderItems;
    }

    public void clearOrder() {
        activeOrderItems.clear();
    }

}
