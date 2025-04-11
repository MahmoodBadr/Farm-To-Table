package com.example.farm_to_table;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static HistoryManager instance;
    private List<Product> historyItems;
    private static final int MAX_HISTORY_ITEMS = 50;
    private HistoryUpdateListener listener;
    private boolean ActiveOrder = false;

    public interface HistoryUpdateListener {
        void onHistoryUpdated();
    }

    private HistoryManager() {
        historyItems = new ArrayList<>();
    }

    public static HistoryManager getInstance() {
        if (instance == null) {
            instance = new HistoryManager();
        }
        return instance;
    }

    public void setHistoryUpdateListener(HistoryUpdateListener listener) {
        this.listener = listener;
    }

    public void addToHistory(Product product) {
        // Remove if product already exists
        historyItems.removeIf(item -> item.getName().equals(product.getName()));

        // Add new product at the beginning
        historyItems.add(0, product);

        // Keep only the last MAX_HISTORY_ITEMS items
        if (historyItems.size() > MAX_HISTORY_ITEMS) {
            historyItems.remove(historyItems.size() - 1);
        }

        // Notify listener
        if (listener != null) {
            listener.onHistoryUpdated();
        }
    }

    public List<Product> getHistoryItems() {
        return historyItems;
    }

    public void clearHistory() {
        historyItems.clear();
        if (listener != null) {
            listener.onHistoryUpdated();
        }
    }
}