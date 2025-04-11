package com.example.farm_to_table;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.farm_to_table.databinding.ActivityOrderSummaryBinding;

public class OrderSummaryActivity extends AppCompatActivity {
    private ActivityOrderSummaryBinding binding;
    private OrderSummaryAdapter orderSummaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderSummaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = binding.rvOrderItems;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        orderSummaryAdapter = new OrderSummaryAdapter(ActiveOrder.getInstance().getActiveOrderItems());
        recyclerView.setAdapter(orderSummaryAdapter);


        updateTotalPrice();

        binding.btnBack.setOnClickListener(v -> {
            startActivity(new Intent(OrderSummaryActivity.this, TrackOrder.class));
            finish();
        });
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderSummaryActivity.this, TrackOrder.class));
        finish();
    }


    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : ActiveOrder.getInstance().getActiveOrderItems()) {
            total += item.getTotalPrice();
        }
        binding.tvTotalAmount.setText(String.format("$%.2f", total));
    }
}