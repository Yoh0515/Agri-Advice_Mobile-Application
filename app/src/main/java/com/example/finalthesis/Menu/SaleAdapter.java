package com.example.finalthesis.Menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.Marketplace.MarketData;
import com.example.finalthesis.R;

import java.util.List;

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.ViewHolder> {
    private List<MarketData> marketplaceItems;
    private Context context;

    public SaleAdapter(List<MarketData> marketplaceItems, Context context) {
        this.marketplaceItems = marketplaceItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketData item = marketplaceItems.get(position);

        // Load the image using Glide
        if (item.getmProduct() != null && !item.getmProduct().isEmpty()) {
            Glide.with(context).load(item.getmProduct().get(0)).into(holder.image);
        }

        // Set other views
        holder.titleTextView.setText(item.getTitle());
        holder.totalItemOrders.setText(String.valueOf(item.getTotalItemOrders()) + " Sold");
    }

    @Override
    public int getItemCount() {
        return marketplaceItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, totalItemOrders;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            image = itemView.findViewById(R.id.mProduct);
            totalItemOrders = itemView.findViewById(R.id.countTextView);
        }
    }
}
