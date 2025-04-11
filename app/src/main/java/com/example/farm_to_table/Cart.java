package com.example.farm_to_table;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm_to_table.databinding.ActivityCartBinding;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Cart extends AppCompatActivity implements CartAdapter.CartItemListener, CartManager.CartUpdateListener {

    private ActivityCartBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use view binding
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Track screen view
        trackScreenView("Cart Screen");

        // Setup UI
        setupUI();

        // Setup buttons
        setupButtons();
        CartManager.getInstance().setCartUpdateListener(this);
        binding.bottomNavInclude.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Cart.this, FarmListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        binding.bottomNavInclude.btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });
        binding.bottomNavInclude.btnTracking.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrackOrder.class);
            startActivity(intent);
        });
        ImageButton btnProfile = binding.bottomNavInclude.btnProfile;
        btnProfile.setOnClickListener(v -> showProfileMenu());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CartManager.getInstance().setCartUpdateListener(null);
    }
    @Override
    public void onCartUpdated(int newCount) {
        if (binding.bottomNavInclude.bottomCartBadge != null) {
            if (newCount > 0) {
                binding.bottomNavInclude.bottomCartBadge.setVisibility(View.VISIBLE);
                binding.bottomNavInclude.bottomCartBadge.setText(String.valueOf(newCount));
            } else {
                binding.bottomNavInclude.bottomCartBadge.setVisibility(View.GONE);
            }
        }
    }


    private void setupUI() {
        // Set up RecyclerView
        RecyclerView recyclerView = binding.rvCartItems;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and set adapter
        cartAdapter = new CartAdapter(CartManager.getInstance().getCartItems(), this);
        recyclerView.setAdapter(cartAdapter);

        // Update total price
        updateTotalPrice();

        // Show empty state if cart is empty
        updateEmptyState();
    }

    private void setupButtons() {
        // Back button
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });

        // Checkout button
        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CartManager.getInstance().getItemCount() > 0) {
                    // Track checkout event
                    trackCheckout(CartManager.getInstance().getTotalPrice());

                    // Navigate to checkout (for this example, just show a toast)
                    Toast.makeText(Cart.this,
                            "Proceeding to checkout with total: $" +
                                    String.format("%.2f", CartManager.getInstance().getTotalPrice()),
                            Toast.LENGTH_LONG).show();

                    // Go the the payment screen
                    Intent intent = new Intent(Cart.this, PaymentActivity.class);
                    intent.putExtra("TOTAL_AMOUNT", CartManager.getInstance().getTotalPrice());
                    startActivity(intent);
                } else {
                    Toast.makeText(Cart.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTotalPrice() {
        double total = CartManager.getInstance().getTotalPrice();
        binding.tvTotalAmount.setText(String.format("$%.2f", total));
    }

    private void updateEmptyState() {
        if (CartManager.getInstance().getItemCount() == 0) {
            binding.emptyCartLayout.setVisibility(View.VISIBLE);
            binding.rvCartItems.setVisibility(View.GONE);
            binding.checkoutLayout.setVisibility(View.GONE);
        } else {
            binding.emptyCartLayout.setVisibility(View.GONE);
            binding.rvCartItems.setVisibility(View.VISIBLE);
            binding.checkoutLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onQuantityChanged() {
        updateTotalPrice();
        cartAdapter.notifyDataSetChanged();
        // Update badge when quantity changes
        onCartUpdated(CartManager.getInstance().getTotalItemCount());
    }

    @Override
    public void onItemRemoved(int position) {
        CartManager.getInstance().removeFromCart(position);
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
        updateEmptyState();

        Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
    }

    private void trackScreenView(String screenName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, this.getClass().getSimpleName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    private void trackCheckout(double value) {
        Bundle bundle = new Bundle();
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, value);
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
        bundle.putInt(FirebaseAnalytics.Param.QUANTITY, CartManager.getInstance().getItemCount());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle);
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