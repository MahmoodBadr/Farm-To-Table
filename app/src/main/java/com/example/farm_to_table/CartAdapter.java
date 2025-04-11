package com.example.farm_to_table;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged();
        void onItemRemoved(int position);
    }

    public CartAdapter(List<CartItem> cartItems, CartItemListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.tvProductName.setText(item.getProductName());
        holder.tvFarmName.setText("From: " + item.getFarmName());
        holder.tvPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvItemTotal.setText(String.format("$%.2f", item.getTotalPrice()));
        holder.ivProductImage.setImageResource(item.getImageResourceId());

        // Set click listeners for quantity change
        holder.btnDecrease.setOnClickListener(v -> {
            int quantity = item.getQuantity();
            if (quantity > 1) {
                item.setQuantity(quantity - 1);
                holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
                holder.tvItemTotal.setText(String.format("$%.2f", item.getTotalPrice()));
                listener.onQuantityChanged();
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int quantity = item.getQuantity();
            item.setQuantity(quantity + 1);
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
            holder.tvItemTotal.setText(String.format("$%.2f", item.getTotalPrice()));
            listener.onQuantityChanged();
        });

        // Set click listener for remove button
        holder.btnRemove.setOnClickListener(v -> {
            listener.onItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvFarmName, tvPrice, tvQuantity, tvItemTotal;
        ImageView ivProductImage;
        ImageButton btnDecrease, btnIncrease, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvFarmName = itemView.findViewById(R.id.tv_farm_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvItemTotal = itemView.findViewById(R.id.tv_item_total);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}