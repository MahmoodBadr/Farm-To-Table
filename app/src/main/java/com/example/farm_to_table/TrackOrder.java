package com.example.farm_to_table;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.farm_to_table.databinding.ActivityTrackOrderBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.location.Geocoder;
import android.location.Address;

import java.util.List;
import java.util.Locale;

public class TrackOrder extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityTrackOrderBinding binding;
    private GoogleMap Map;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        binding = ActivityTrackOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ActiveOrder.isOrderActive) {
            binding.NoOrderLayout.setVisibility(View.GONE);
            binding.OpenChat.setVisibility(View.VISIBLE);
            binding.Editadress.setVisibility(View.VISIBLE);
            binding.CancelOrder.setVisibility(View.VISIBLE);
            binding.textView.setVisibility(View.VISIBLE);
            binding.textView4.setVisibility(View.VISIBLE);
            binding.textView5.setVisibility(View.VISIBLE);
            binding.textView6.setVisibility(View.VISIBLE);
            binding.textAdress.setVisibility(View.VISIBLE);
            binding.trackMapView.setVisibility(View.VISIBLE);
            binding.btnOrderSumm.setVisibility(View.VISIBLE);
        } else {
            binding.NoOrderLayout.setVisibility(View.VISIBLE);
            binding.OpenChat.setVisibility(View.GONE);
            binding.Editadress.setVisibility(View.GONE);
            binding.CancelOrder.setVisibility(View.GONE);
            binding.textView.setVisibility(View.GONE);
            binding.textView4.setVisibility(View.GONE);
            binding.textView5.setVisibility(View.GONE);
            binding.textView6.setVisibility(View.GONE);
            binding.textAdress.setVisibility(View.GONE);
            binding.trackMapView.setVisibility(View.GONE);
            binding.btnOrderSumm.setVisibility(View.GONE);
        }

        mapView = findViewById(R.id.trackMapView);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState); // Pass the saved instance state
            mapView.getMapAsync(this); // Set up the map asynchronously
        }

        setupButtons();

        binding.Editadress.setOnClickListener(v -> showAddressEditDialog());
        binding.CancelOrder.setOnClickListener(v -> showcancel());

        binding.bottomNavInclude.btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(TrackOrder.this, FarmListActivity.class);
            startActivity(intent);
            finish();
        });
        binding.bottomNavInclude.btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(TrackOrder.this, HistoryActivity.class);
            startActivity(intent);
            finish();
        });
        binding.bottomNavInclude.btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(TrackOrder.this, Cart.class);
            startActivity(intent);
            finish();
        });
        binding.btnOrderSumm.setOnClickListener(v -> {
            Intent intent = new Intent(TrackOrder.this, OrderSummaryActivity.class);
            startActivity(intent);
            finish();
        });
        binding.OpenChat.setOnClickListener(v -> {
            Intent intent = new Intent(TrackOrder.this, PhotoConfirmation.class);
            startActivity(intent);
            finish();
        });
        ImageButton btnProfile = binding.bottomNavInclude.btnProfile;
        btnProfile.setOnClickListener(v -> showProfileMenu());
    }

    private void setupButtons() {
        // Back button
        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(TrackOrder.this, FarmListActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Map = googleMap;

        // Add a marker at "333 University Way"
        LatLng universityLocation = new LatLng(49.94025, -119.3946); // Example coordinates for "333 University Way"
        Map.addMarker(new MarkerOptions().position(universityLocation).title("3333 University Way"));
        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(universityLocation, 11)); // Zoom into the location
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume(); // Resume the map when the activity is resumed
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause(); // Pause the map when the activity is paused
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy(); // Clean up when the activity is destroyed
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory(); // Handle low memory situations
        }
    }

    private void showAddressEditDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.edit_address, null);
        EditText input = dialogView.findViewById(R.id.editAddressInput);

        new AlertDialog.Builder(this)
                .setTitle("Edit Delivery Address").setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String newAddress = input.getText().toString().trim();
                    if (!newAddress.isEmpty()) {
                        updateMapMarker(newAddress);
                    }}).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
    }

    private void updateMapMarker(String addressStr) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(addressStr, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());

                Map.clear();
                Map.addMarker(new MarkerOptions().position(newLocation).title(addressStr));
                Map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 11));

                binding.textAdress.setText(addressStr);
            } else {
                Toast.makeText(this, "Address not found.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to update map. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showcancel() {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure you wish to cancel your order?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    ActiveOrder.isOrderActive = false;
                    recreate();
                }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
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