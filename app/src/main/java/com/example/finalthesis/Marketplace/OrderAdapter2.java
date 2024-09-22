package com.example.finalthesis.Marketplace;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter2 extends RecyclerView.Adapter<OrderAdapter2.MyViewHolder> {

    private ArrayList<OrderData2> orderData;
    private Context context;

    public OrderAdapter2(ArrayList<OrderData2> orderData, Context context){
        this.orderData = orderData;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter2.MyViewHolder holder, int position) {
        holder.mTitle.setText(orderData.get(position).getUserName());
        holder.date.setText(orderData.get(position).getPaymentDate());
        Glide.with(context).load(orderData.get(position).getImageUrl()).into(holder.imageView);
        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderConfirmed.class);
                intent.putExtra("Image", orderData.get(holder.getAdapterPosition()).getImageUrl());
                intent.putExtra("Date", orderData.get(holder.getAdapterPosition()).getPaymentDate());
                intent.putExtra("Reference", orderData.get(holder.getAdapterPosition()).getReferenceNumber());
                intent.putExtra("Price", orderData.get(holder.getAdapterPosition()).getTotalPrice());
                intent.putExtra("Trans", orderData.get(holder.getAdapterPosition()).getTransactionId());
                intent.putExtra("Name", orderData.get(holder.getAdapterPosition()).getUserName());
                intent.putExtra("Vendor", orderData.get(holder.getAdapterPosition()).getVendor());
                intent.putExtra("Key",orderData.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });
    }

    public void updateData(List<OrderData2> newData) {
        orderData.clear();
        orderData.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public void searchDataList(ArrayList<OrderData2> searchList){
        orderData = searchList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle, date;
        ImageView imageView;
        LinearLayout mCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.buyerName);
            date = itemView.findViewById(R.id.date);
            imageView = itemView.findViewById(R.id.buyerImage);
            mCard = itemView.findViewById(R.id.orderLayout);
        }
    }
}