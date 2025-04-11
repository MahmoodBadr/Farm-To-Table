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

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.OrderSummaryViewHolder> {

    private List<CartItem> activeOrderItems;

    public OrderSummaryAdapter(List<CartItem> activeOrderItems) {
        this.activeOrderItems = activeOrderItems;
    }

    @NonNull
    @Override
    public OrderSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderSummaryViewHolder holder, int position) {
        CartItem item = activeOrderItems.get(position);
        holder.tvProductName.setText(item.getProductName());
        holder.tvFarmName.setText("From: " + item.getFarmName());
        holder.tvPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvItemTotal.setText(String.format("$%.2f", item.getTotalPrice()));
        holder.ivProductImage.setImageResource(item.getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return activeOrderItems.size();
    }

    static class OrderSummaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvFarmName, tvPrice, tvQuantity, tvItemTotal;
        ImageView ivProductImage;

        public OrderSummaryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvFarmName = itemView.findViewById(R.id.tv_farm_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvItemTotal = itemView.findViewById(R.id.tv_item_total);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
        }
    }
}