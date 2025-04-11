package com.example.farm_to_table;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvSubtotalValue, tvDeliveryValue, tvTotalValue;
    private RadioGroup rgPaymentOptions;
    private Button btnPay;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize views
        initializeViews();
        setupToolbar();
        setupPaymentDetails();

        btnPay.setOnClickListener(v -> {
            if (validatePayment()) {
                showConfirmationDialog();
            }
        });
    }

    private void initializeViews() {
        tvSubtotalValue = findViewById(R.id.tvSubtotalValue);
        tvDeliveryValue = findViewById(R.id.tvDeliveryValue);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        rgPaymentOptions = findViewById(R.id.rgPaymentOptions);
        btnPay = findViewById(R.id.btnPay);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupPaymentDetails() {
        totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0.0);
        double deliveryFee = 2.00;
        double subtotal = totalAmount - deliveryFee;

        tvSubtotalValue.setText(String.format("$%.2f", subtotal));
        tvDeliveryValue.setText(String.format("$%.2f", deliveryFee));
        tvTotalValue.setText(String.format("$%.2f", totalAmount));
        btnPay.setText("Pay " + String.format("$%.2f", totalAmount));
    }


    private boolean validatePayment() {
        if (rgPaymentOptions.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Payment")
                .setMessage("Do you wish to proceed with the payment of $" + String.format("%.2f", totalAmount) + "?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    processPayment();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void processPayment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View processingView = LayoutInflater.from(this).inflate(R.layout.dialogmessage, null);
        builder.setView(processingView);
        builder.setCancelable(false);
        AlertDialog processingDialog = builder.create();
        processingDialog.show();

        // Simulate payment processing
        new android.os.Handler().postDelayed(() -> {
            processingDialog.dismiss();
            paymentSuccess();
        }, 2000);
    }

    private void paymentSuccess() {
        // Save order state
        SharedPreferences.Editor editor = getSharedPreferences("AppSettings", MODE_PRIVATE).edit();
        editor.putBoolean("isOrderActive", true);
        editor.putString("deliveryAddress", "Your delivery address");
        editor.apply();
      
        // Copy to order, and activate order tracking screen
        ActiveOrder.getInstance().copyFromCart(CartManager.getInstance().getCartItems());
        ActiveOrder.isOrderActive = true;

        // Clear cart
        CartManager.getInstance().clearCart();

        // Show success dialog
        new AlertDialog.Builder(this)
                .setTitle("Payment Successful!")
                .setMessage("Your order has been placed successfully.")
                .setPositiveButton("Track Order", (dialog, which) -> {
                    startActivity(new Intent(PaymentActivity.this, TrackOrder.class));
                    finish();
                })
                .setNeutralButton("Continue Shopping", (dialog, which) -> {
                    startActivity(new Intent(PaymentActivity.this, FarmListActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}