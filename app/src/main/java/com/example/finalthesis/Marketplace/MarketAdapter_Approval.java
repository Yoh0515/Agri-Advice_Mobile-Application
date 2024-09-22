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

public class MarketAdapter_Approval extends RecyclerView.Adapter<MarketAdapter_Approval.MyViewHolder> {

    private ArrayList<MarketData> marketList;
    private Context context;

    public MarketAdapter_Approval(ArrayList<MarketData> marketList,Context context){
        this.marketList = marketList;
        this.context = context;
    }

    @NonNull
    @Override
    public MarketAdapter_Approval.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.approval_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketAdapter_Approval.MyViewHolder holder, int position) {
        Glide.with(context).load(marketList.get(position).getmProduct().get(0)).into(holder.mImage);
        holder.mTitle.setText(marketList.get(position).getTitle());
        holder.mPrice.setText(marketList.get(position).getPrice());
        holder.mUnit.setText(marketList.get(position).getUnit());
        holder.approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Product_Detail_VendorView.class);
                intent.putStringArrayListExtra("Image", new ArrayList<>(marketList.get(holder.getAdapterPosition()).getmProduct()));
                intent.putExtra("Description", marketList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("Title", marketList.get(holder.getAdapterPosition()).getTitle());
                intent.putExtra("Name", marketList.get(holder.getAdapterPosition()).getVendor());
                intent.putExtra("Unit", marketList.get(holder.getAdapterPosition()).getUnit());
                intent.putExtra("Price", marketList.get(holder.getAdapterPosition()).getPrice());
                intent.putExtra("Location", marketList.get(holder.getAdapterPosition()).getLocation());
                intent.putExtra("Qyt", marketList.get(holder.getAdapterPosition()).getQuantity());
                intent.putExtra("Key",marketList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });

//        String isApproved = marketList.get(position).getCategory();
//
//        if (isApproved.equals("2")) {
//            // If isApproved is true, set background color to green
//            holder.mBgApproval.setBackgroundColor(context.getResources().getColor(R.color.green));
//            holder.mBgApproval.setText("Approved");
//        } else if (isApproved.equals("3")) {
//            holder.mBgApproval.setBackgroundColor(context.getResources().getColor(R.color.red));
//            holder.mBgApproval.setText("Declined");
//        }else {// If isApproved is false, set background color to red
//            holder.mBgApproval.setBackgroundColor(context.getResources().getColor(R.color.orange)); // Replace R.color.red with your red color resource
//        }

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

    public void searchDataList(ArrayList<MarketData> searchList){
        marketList = searchList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mImage;
        TextView mTitle, mPrice, mBgApproval, mUnit;
        LinearLayout mCard;
        RelativeLayout approval;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.recycleImage3);
            mTitle = itemView.findViewById(R.id.title3);
            mUnit = itemView.findViewById(R.id.mUnit);
            mPrice = itemView.findViewById(R.id.price3);
            approval = itemView.findViewById(R.id.approvalItem);
//            mBgApproval = itemView.findViewById(R.id.bgApproval);

        }
    }
}

