package com.example.farm_to_table;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;
    private CartUpdateListener listener;

    public interface CartUpdateListener {
        void onCartUpdated(int newCount);
    }

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void setCartUpdateListener(CartUpdateListener listener) {
        this.listener = listener;
    }

    public int getTotalItemCount() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    public void addToCart(CartItem item) {
        boolean itemExists = false;

        // Check if product already exists in cart
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductName().equals(item.getProductName()) &&
                    cartItem.getFarmName().equals(item.getFarmName())) {
                // Increment quantity by 1
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                itemExists = true;
                break;
            }
        }

        // If not found, add as a new item
        if (!itemExists) {
            cartItems.add(item);
        }

        // Notify listeners about cart update
        if (listener != null) {
            listener.onCartUpdated(getTotalItemCount());
        }
    }
    public void removeQuantity(String productName, String farmName, int quantityToRemove) {
        CartItem itemToRemove = null;

        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductName().equals(productName) &&
                    cartItem.getFarmName().equals(farmName)) {
                int newQuantity = cartItem.getQuantity() - quantityToRemove;
                if (newQuantity <= 0) {
                    itemToRemove = cartItem;
                } else {
                    cartItem.setQuantity(newQuantity);
                }
                break;
            }
        }

        // Remove item outside the loop to avoid ConcurrentModificationException
        if (itemToRemove != null) {
            cartItems.remove(itemToRemove);
        }

        // Always notify listener after modification
        if (listener != null) {
            listener.onCartUpdated(getTotalItemCount());
        }
    }


    public void removeFromCart(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            // Notify listener of item removal
            if (listener != null) {
                listener.onCartUpdated(getTotalItemCount());
            }
        }
    }


    public void updateQuantity(int position, int quantity) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.get(position).setQuantity(quantity);
            // Notify listener of quantity change
            if (listener != null) {
                listener.onCartUpdated(getTotalItemCount());
            }
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public int getItemCount() {
        return cartItems.size();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }
}