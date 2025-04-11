package com.example.farm_to_table;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farm_to_table.databinding.ActivityProductDetailBinding;
import com.google.firebase.analytics.FirebaseAnalytics;

public class ProductDetail extends AppCompatActivity implements CartManager.CartUpdateListener,HistoryManager.HistoryUpdateListener {

    private ActivityProductDetailBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;

    // Product data
    private String productName;
    private String productDescription;
    private double productPrice;
    private int productImageId;
    private String farmName;
    private int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use view binding
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        binding.bottomNavInclude.btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });
        binding.bottomNavInclude.btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetail.this, FarmListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        binding.bottomNavInclude.btnTracking.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetail.this, TrackOrder.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        ImageButton btnProfile = binding.bottomNavInclude.btnProfile;
        btnProfile.setOnClickListener(v -> showProfileMenu());

        // Track screen view
        trackScreenView("Product Detail Screen");
        HistoryManager.getInstance().setHistoryUpdateListener(this);
        // Get product data from intent
        Intent intent = getIntent();
        if (intent != null) {
            productName = intent.getStringExtra("PRODUCT_NAME");
            productDescription = intent.getStringExtra("PRODUCT_DESCRIPTION");
            productPrice = intent.getDoubleExtra("PRODUCT_PRICE", 0.0);
            productImageId = intent.getIntExtra("PRODUCT_IMAGE", R.drawable.product);
            farmName = intent.getStringExtra("FARM_NAME");
        }
        HistoryManager.getInstance().addToHistory(
                new Product(productName, productDescription, productPrice, productImageId, farmName)
        );


        // Set up the UI with product data
        setupUI();

        // Set up button click listeners
        setupButtons();

        // Update cart badge
        updateCartBadge();
        CartManager.getInstance().setCartUpdateListener(this);

    }

    @Override
    public void onHistoryUpdated() {

    }

    protected void onDestroy() {
        super.onDestroy();
        // Remove listener
        CartManager.getInstance().setCartUpdateListener(null);
        HistoryManager.getInstance().setHistoryUpdateListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update cart badge when returning to this screen
        updateCartBadge();
    }
    private void updateCartBadge() {
        if (binding.bottomNavInclude.bottomCartBadge != null) {
            int totalItems = CartManager.getInstance().getTotalItemCount();
            if (totalItems > 0) {
                binding.bottomNavInclude.bottomCartBadge.setVisibility(View.VISIBLE);
                binding.bottomNavInclude.bottomCartBadge.setText(String.valueOf(totalItems));
            } else {
                binding.bottomNavInclude.bottomCartBadge.setVisibility(View.GONE);
            }
        }
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
        // Set product details
        binding.tvProductName.setText(productName);
        binding.tvProductDescription.setText(productDescription);
        binding.tvProductPrice.setText(String.format("$%.2f", productPrice));
        binding.ivProductImage.setImageResource(productImageId);
        binding.tvFarmName.setText("From: " + farmName);
        binding.tvQuantity.setText(String.valueOf(quantity));
        updateTotalPrice();
    }

    private void setupButtons() {
        // Back button
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });

        // Decrease quantity button
        binding.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    quantity--;
                    binding.tvQuantity.setText(String.valueOf(quantity));
                    updateTotalPrice();
                }
            }
        });


        // Increase quantity button
        binding.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                binding.tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        // Add to cart button
        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    // Track event
                    trackAddToCart(productName, quantity, productPrice * quantity);

                    // Add to cart with current quantity
                    CartManager.getInstance().addToCart(
                            new CartItem(productName, productPrice, quantity, productImageId, farmName)
                    );

                    Toast.makeText(ProductDetail.this,
                            quantity + " " + productName + " added to cart",
                            Toast.LENGTH_SHORT).show();

                    // Update cart badge
                    updateCartBadge();

                    // Reset quantity to 0
                    quantity = 0;
                    binding.tvQuantity.setText(String.valueOf(quantity));
                    updateTotalPrice();
                } else {
                    Toast.makeText(ProductDetail.this,
                            "Please select quantity first",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cart icon click listener (if you have one in the layout)
        binding.bottomNavInclude.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to cart
                Intent intent = new Intent(ProductDetail.this, Cart.class);
                startActivity(intent);
            }
        });
        binding.bottomNavInclude.bottomCartBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetail.this, FarmListActivity.class);
                startActivity(intent);
            }
        });
    }



    private void updateTotalPrice() {
        double total = productPrice * quantity;
        binding.tvTotalPrice.setText(String.format("Total: $%.2f", total));
    }

    private void trackScreenView(String screenName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, this.getClass().getSimpleName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }
    private void trackAddToCart(String productName, int quantity, double total) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productName);
        bundle.putInt(FirebaseAnalytics.Param.QUANTITY, quantity);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, total);
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "CAD");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
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