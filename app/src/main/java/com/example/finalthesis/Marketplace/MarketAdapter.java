package com.example.finalthesis.Marketplace;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;

import java.util.ArrayList;
import java.util.List;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.MyViewHolder> {

    private ArrayList<MarketData> marketList;
    private Context context;

    public MarketAdapter(ArrayList<MarketData> marketList, Context context) {
        this.marketList = marketList;
        this.context = context;
    }

    @NonNull
    @Override
    public MarketAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_market, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketAdapter.MyViewHolder holder, int position) {
        MarketData marketData = marketList.get(position);

        // Load the first image from the mProduct list
        if (marketData.getmProduct() != null && !marketData.getmProduct().isEmpty()) {
            Glide.with(context).load(marketData.getmProduct().get(0)).into(holder.mImage);
        }
        holder.mTitle.setText(marketData.getTitle());
        holder.mPrice.setText(marketData.getPrice());
        holder.mUnit.setText(marketData.getUnit());

        // Display totalItemOrders as "Sold"
        holder.mSold.setText(marketData.getTotalItemOrders() + " Sold");

        // Calculate average rating and review count
        if (marketData.getFeedbackArray() != null && !marketData.getFeedbackArray().isEmpty()) {
            int totalReviews = marketData.getFeedbackArray().size();
            int sumRating = 0;

            for (MarketData.Feedback feedback : marketData.getFeedbackArray()) {
                sumRating += feedback.getRating();
            }

            double averageRating = (double) sumRating / totalReviews;
            holder.mRating.setText(String.format("%.1f", averageRating));
            holder.mReviewCount.setText("(" + totalReviews + " reviews)");
        } else {
            holder.mRating.setText("N/A");
            holder.mReviewCount.setText("(0 reviews)");
        }

        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Product_Detail.class);
                intent.putStringArrayListExtra("Image", new ArrayList<>(marketList.get(holder.getAdapterPosition()).getmProduct()));
                intent.putExtra("Description", marketList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("Title", marketList.get(holder.getAdapterPosition()).getTitle());
                intent.putExtra("Name", marketList.get(holder.getAdapterPosition()).getVendor());
                intent.putExtra("Unit", marketList.get(holder.getAdapterPosition()).getUnit());
                intent.putExtra("Price", marketList.get(holder.getAdapterPosition()).getPrice());
                intent.putExtra("Location", marketList.get(holder.getAdapterPosition()).getLocation());
                intent.putExtra("Qyt", marketList.get(holder.getAdapterPosition()).getQuantity());
                intent.putExtra("Key", marketList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });
    }

    public void updateData(List<MarketData> newData) {
        marketList.clear();
        marketList.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return marketList.size();
    }

    public void searchDataList(ArrayList<MarketData> searchList) {
        marketList = searchList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mImage;
        TextView mTitle, mPrice, mUnit, mRating, mReviewCount, mSold; // Add mSold
        LinearLayout mCard;
        RelativeLayout approval;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.mProduct);
            mUnit = itemView.findViewById(R.id.unit);
            mTitle = itemView.findViewById(R.id.mTitle);
            mPrice = itemView.findViewById(R.id.mPrice);
            mCard = itemView.findViewById(R.id.mCard);
            approval = itemView.findViewById(R.id.approvalItem);
            mRating = itemView.findViewById(R.id.mRating);
            mReviewCount = itemView.findViewById(R.id.mReviewCount);
            mSold = itemView.findViewById(R.id.mSold); // Add this line
        }
    }
}
