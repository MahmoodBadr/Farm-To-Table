package com.example.farm_to_table;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {
    private List<Product> products;
    private OnProductClickListener listener;
    private List<Product> productsFilterList;

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.productsFilterList = new ArrayList<>(products);
        this.listener = listener;
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Product> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(productsFilterList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Product product : productsFilterList) {
                        if (product.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(product);
                        }




                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                products.clear();
                products.addAll((List<Product>) results.values);
                notifyDataSetChanged();
                if (products.isEmpty()) {
                    onFilterListener.onEmptyResult();
                } else {
                    onFilterListener.onResult();
                }

            }
        };
    }
    public interface OnFilterListener {
        void onEmptyResult();
        void onResult();
    }

    private OnFilterListener onFilterListener;

    public void setOnFilterListener(OnFilterListener listener) {
        this.onFilterListener = listener;
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.iterm_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private TextView productDescription;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views using findViewById
            productImage = itemView.findViewById(R.id.iv_product);
            productName = itemView.findViewById(R.id.tv_product_name);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            productDescription = itemView.findViewById(R.id.tv_product_description);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onProductClick(products.get(position));
                }
            });
        }

        void bind(Product product) {
            if (productImage != null) {
                productImage.setImageResource(product.getImageResourceId());
            }
            if (productName != null) {
                productName.setText(product.getName());
            }
            if (productPrice != null) {
                productPrice.setText(String.format("$%.2f", product.getPrice()));
            }
            if (productDescription != null) {
                productDescription.setText(product.getDescription());
            }
        }
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }


}

