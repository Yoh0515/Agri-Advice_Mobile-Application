package com.example.finalthesis.PlantingGuidance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;

import java.util.ArrayList;

public class PlantAdapter2 extends RecyclerView.Adapter<PlantAdapter2.MyViewHolder> {

    Context context;
    ArrayList<PlantData> PlantList;

    public PlantAdapter2(Context context, ArrayList<PlantData> plantList) {
        this.context = context;
        this.PlantList = plantList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plant2,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {

        return PlantList.size();
    }

    public void searchDataList(ArrayList<PlantData> searchList){
        PlantList = searchList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context).load(PlantList.get(position).getIconUrl()).into(holder.iconImage);
        holder.titleTextView.setText(PlantList.get(position).getTitle());
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView titleTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iconImage = itemView.findViewById(R.id.icon_image);
            titleTextView = itemView.findViewById(R.id.title_text_view);
        }
    }

}