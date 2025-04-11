
package com.example.farm_to_table;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.farm_to_table.HistoryManager;
import com.example.farm_to_table.Product;
import com.example.farm_to_table.ProductAdapter;
import com.example.farm_to_table.databinding.ActivityHistoryBinding;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {
    private ActivityHistoryBinding binding;
    private ProductAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupButtons();
        binding.bottomNavInclude.btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, FarmListActivity.class);
            startActivity(intent);
            finish();
        });
        binding.bottomNavInclude.btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, HistoryActivity.class);
            startActivity(intent);
            finish();
        });
        binding.bottomNavInclude.btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, Cart.class);
            startActivity(intent);
            finish();
        });
        binding.bottomNavInclude.btnTracking.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, TrackOrder.class);
            startActivity(intent);
            finish();
        });
        ImageButton btnProfile = binding.bottomNavInclude.btnProfile;
        btnProfile.setOnClickListener(v -> showProfileMenu());
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.rvHistory;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Product> historyItems = HistoryManager.getInstance().getHistoryItems();
        historyAdapter = new ProductAdapter(historyItems, this);
        recyclerView.setAdapter(historyAdapter);

        // Show/hide empty state
        updateEmptyState();
    }

    private void setupButtons() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnClearHistory.setOnClickListener(v -> {
            HistoryManager.getInstance().clearHistory();
            historyAdapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

    private void updateEmptyState() {
        List<Product> historyItems = HistoryManager.getInstance().getHistoryItems();
        if (historyItems.isEmpty()) {
            binding.emptyHistoryLayout.setVisibility(View.VISIBLE);
            binding.rvHistory.setVisibility(View.GONE);
        } else {
            binding.emptyHistoryLayout.setVisibility(View.GONE);
            binding.rvHistory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetail.class);
        intent.putExtra("PRODUCT_NAME", product.getName());
        intent.putExtra("PRODUCT_DESCRIPTION", product.getDescription());
        intent.putExtra("PRODUCT_PRICE", product.getPrice());
        intent.putExtra("PRODUCT_IMAGE", product.getImageResourceId());
        intent.putExtra("FARM_NAME", product.getFarmName());
        startActivity(intent);
    }
    private void showProfileMenu() {
        PopupMenu popup = new PopupMenu(this, binding.bottomNavInclude.btnProfile);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_logout) {
                logout();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void logout() {
        // Clear all saved data
        getSharedPreferences("AppSettings", MODE_PRIVATE).edit().clear().apply();

        // Clear cart and history
        CartManager.getInstance().clearCart();
        HistoryManager.getInstance().clearHistory();

        // Navigate to login screen
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}