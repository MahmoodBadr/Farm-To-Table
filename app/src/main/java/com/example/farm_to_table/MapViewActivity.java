    package com.example.farm_to_table;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageButton;
    import android.widget.PopupMenu;

    import androidx.appcompat.app.AppCompatActivity;

    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.BitmapDescriptorFactory;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.google.android.gms.maps.model.Marker;
    import com.google.firebase.analytics.FirebaseAnalytics;
    import com.example.farm_to_table.databinding.ActivityMapViewBinding;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback, CartManager.CartUpdateListener {
        private GoogleMap map;
        private FirebaseAnalytics mFirebaseAnalytics;
        private Button selectFarmButton;
        private ActivityMapViewBinding binding;
        private List<Farm> farms = new ArrayList<>();
        private Map<String, Farm> farmMap = new HashMap<>(); // To track which marker belongs to which farm

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Use view binding instead of findViewById
            binding = ActivityMapViewBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Initialize Firebase Analytics
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            // Track screen view
            trackScreenView("Map View Screen");

            // Using view binding to access UI elements
            selectFarmButton = binding.btnSelectFarm;

            // Set up the map
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            // Add sample farms in Kelowna area
            setupFarmData();

            // Set up button click listeners
            selectFarmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to farm list activity
                    startActivity(new Intent(MapViewActivity.this, FarmListActivity.class));

                    // Track event
                    Bundle bundle = new Bundle();
                    bundle.putString("button_name", "select_farm_button");
                    mFirebaseAnalytics.logEvent("map_button_click", bundle);
                }
            });

            ImageButton btnProfile = binding.bottomNavInclude.btnProfile;
            btnProfile.setOnClickListener(v -> showProfileMenu());
            // Set up bottom navigation
            setupBottomNavigation();
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

        private void setupFarmData() {
            // Kelowna area farm data
            farms.add(new Farm("Paynter's Fruit Market", new LatLng(49.8342, -119.6501), "cherries and apples",R.drawable.cherries));
            farms.add(new Farm("Arlo's Honey Farm", new LatLng(49.8887, -119.4962), "Local honey producer with bee tours and natural products",R.drawable.honey));
            farms.add(new Farm("Okanagan Lavender & Herb Farm", new LatLng(49.8348, -119.4887), "herb gardens with artisanal products",R.drawable.herbal));
            farms.add(new Farm("Don-O-Ray Vegetables", new LatLng(49.8883, -119.4015), "Local vegetable farm with fresh seasonal produce",R.drawable.vegetables));
            farms.add(new Farm("Hillcrest Farm Market", new LatLng(50.0283, -119.4143), "fresh fruits and vegetables",R.drawable.fruits));

            // Populate the farmMap for marker lookup
            for (Farm farm : farms) {
                farmMap.put(farm.getName(), farm);
            }
        }

        private void setupBottomNavigation() {
            ImageButton homeBtn = binding.bottomNavInclude.btnHome;
            ImageButton trackingBtn = binding.bottomNavInclude.btnTracking;
            ImageButton cartBtn = binding.bottomNavInclude.btnCart;
            ImageButton historyBtn = binding.bottomNavInclude.btnHistory;
            ImageButton settingsBtn = binding.bottomNavInclude.btnProfile;

            // Implement these once you have the activities created
            homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MapViewActivity.this, FarmListActivity.class));
                }
            });
            cartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MapViewActivity.this, Cart.class));

                    // Track event
                    Bundle bundle = new Bundle();
                    bundle.putString("button_name", "cart_bottom_nav");
                    mFirebaseAnalytics.logEvent("navigation_click", bundle);
                }
            });
            historyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MapViewActivity.this, HistoryActivity.class));

                    // Track event
                    Bundle bundle = new Bundle();
                    bundle.putString("button_name", "history_bottom_nav");
                    mFirebaseAnalytics.logEvent("navigation_click", bundle);
                }
            });
            trackingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MapViewActivity.this, TrackOrder.class));
                }
            });

        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Add markers for farms with orange color
            for (Farm farm : farms) {
                map.addMarker(new MarkerOptions()
                        .position(farm.getLocation())
                        .title(farm.getName())
                        .snippet(farm.getDescription())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }

            // Center map on Kelowna area (adjust zoom level to show all markers)
            LatLng kelownaCenter = new LatLng(49.8951, -119.4947);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(kelownaCenter, 10));

            // Configure map UI settings
            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setCompassEnabled(true);

            // Set up marker click listener for analytics
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    trackFarmSelection(marker.getTitle());
                    marker.showInfoWindow();
                    return true; // true means we handled the event
                }
            });

            // Set up info window click listener to navigate to farm's activity
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    // Get the selected farm
                    Farm selectedFarm = farmMap.get(marker.getTitle());
                    if (selectedFarm != null) {
                        // Navigate to FarmDetailActivity
                        Intent intent = new Intent(MapViewActivity.this, FarmDetailActivity.class);
                        intent.putExtra("FARM_NAME", selectedFarm.getName());
                        intent.putExtra("FARM_DESCRIPTION", selectedFarm.getDescription());
                        intent.putExtra("FARM_LAT", selectedFarm.getLocation().latitude);
                        intent.putExtra("FARM_LNG", selectedFarm.getLocation().longitude);
                        intent.putExtra("FARM_IMAGE", selectedFarm.getImageResourceId());
                        startActivity(intent);
                    }
                }
            });
        }

        private void trackScreenView(String screenName) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, this.getClass().getSimpleName());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
        }

        private void trackFarmSelection(String farmName) {
            Bundle bundle = new Bundle();
            bundle.putString("farm_name", farmName);
            mFirebaseAnalytics.logEvent("farm_marker_selected", bundle);
        }

        // Farm data model
        public static class Farm {
            private String name;
            private LatLng location;
            private String description;
            private int images;

            public Farm(String name, LatLng location, String description,int images) {
                this.name = name;
                this.location = location;
                this.description = description;
                this.images=images;
            }

            public String getName() {
                return name;
            }

            public LatLng getLocation() {
                return location;
            }

            public String getDescription() {
                return description;
            }
            public int getImageResourceId() {
                return images;
            }
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