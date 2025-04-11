package com.example.farm_to_table;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.example.farm_to_table.databinding.ActivityFarmDetailBinding;

public class FarmDetailActivity extends AppCompatActivity implements OnMapReadyCallback, CartManager.CartUpdateListener {

    private ActivityFarmDetailBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private GoogleMap map;

    // Farm data
    private String farmName;
    private String farmDescription;
    private double farmLat;
    private double farmLng;
    private int farmImageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use view binding
        binding = ActivityFarmDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Track screen view
        trackScreenView("Farm Detail Screen");

        // Get farm data from intent
        Intent intent = getIntent();
        if (intent != null) {
            farmName = intent.getStringExtra("FARM_NAME");
            farmDescription = intent.getStringExtra("FARM_DESCRIPTION");
            farmLat = intent.getDoubleExtra("FARM_LAT", 0);
            farmLng = intent.getDoubleExtra("FARM_LNG", 0);
            farmImageId = intent.getIntExtra("FARM_IMAGE", R.drawable.product);
        }

        // Set up the UI with farm data
        setupUI();

        // Set up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up button click listeners
        setupButtons();

        // Update cart badge
        updateCartBadge();
        CartManager.getInstance().setCartUpdateListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Update cart badge when returning to this activity
        updateCartBadge();
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


    private void updateCartBadge() {
        // Get the current cart count
        int cartCount = CartManager.getInstance().getTotalItemCount();
        if (binding.bottomNavInclude.bottomCartBadge != null) {
            if (cartCount > 0) {
                binding.bottomNavInclude.bottomCartBadge.setVisibility(View.VISIBLE);
                binding.bottomNavInclude.bottomCartBadge.setText(String.valueOf(cartCount));
            } else {
                binding.bottomNavInclude.bottomCartBadge.setVisibility(View.GONE);
            }
        }
    }

    private void setupUI() {
        // Set farm name and description
        binding.tvFarmName.setText(farmName);
        binding.tvFarmDescription.setText(farmDescription);
        binding.ivFarmImage.setImageResource(farmImageId);
    }

    private void setupButtons() {
        // Back button
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });

        //Call farm button
        binding.btnVisitWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Track event
                trackButtonClick("call_farm_button");

                // phone number
                 Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:2363384211"));
                startActivity(callIntent);
            }
        });

        // Get directions button
        binding.btnGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Track event
                trackButtonClick("get_directions_button");

                // Use driving mode and current location
                String uri = String.format("google.navigation:q=%f,%f&mode=d", farmLat, farmLng);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        // View Products button
        binding.btnViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Track event
//                trackButtonClick("view_products_button");

                // Navigate to product list activity with correct extras
                Intent intent = new Intent(FarmDetailActivity.this,ProductListActivity.class);
                intent.putExtra("FARM_NAME", farmName);
                intent.putExtra("FARM_DESCRIPTION", farmDescription);
                intent.putExtra("FARM_IMAGE", farmImageId);
                startActivity(intent);
            }
        });

        // Cart button in bottom navigation
        binding.bottomNavInclude.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Track event
                trackButtonClick("cart_nav_button");

                // Navigate to cart activity
                Intent intent = new Intent(FarmDetailActivity.this, Cart.class);
                startActivity(intent);
            }
        });

        // Home button in bottom navigation
        binding.bottomNavInclude.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to main activity
                Intent intent = new Intent(FarmDetailActivity.this, FarmListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        binding.bottomNavInclude.btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });
        ImageButton btnProfile = binding.bottomNavInclude.btnProfile;
        btnProfile.setOnClickListener(v -> showProfileMenu());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add marker for the farm
        LatLng farmLocation = new LatLng(farmLat, farmLng);
        map.addMarker(new MarkerOptions()
                .position(farmLocation)
                .title(farmName));

        // Zoom to the farm location
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(farmLocation, 15));

        // Configure map UI settings
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
    }

    private void trackScreenView(String screenName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, this.getClass().getSimpleName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    private void trackButtonClick(String buttonName) {
        Bundle bundle = new Bundle();
        bundle.putString("button_name", buttonName);
        bundle.putString("farm_name", farmName);
        mFirebaseAnalytics.logEvent("farm_detail_button_click", bundle);
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