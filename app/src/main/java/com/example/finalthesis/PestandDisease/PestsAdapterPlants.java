package com.example.finalthesis.PestandDisease;

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
import com.example.finalthesis.PlantingGuidance.PlantData;
import com.example.finalthesis.PlantingGuidance.Plant_Detail;
import com.example.finalthesis.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class PestsAdapterPlants extends RecyclerView.Adapter<PestsAdapterPlants.MyViewHolder> {

    Context context;
    ArrayList<PestData> PestList;

    public PestsAdapterPlants(Context context, ArrayList<PestData> PestList) {
        this.context = context;
        this.PestList = PestList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pests,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {

        return PestList.size();
    }

    public void searchDataList(ArrayList<PestData> searchList){
        PestList = searchList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context).load(PestList.get(position).getImageUrl()).into(holder.iconImage);
        holder.titleTextView.setText(PestList.get(position).getTitle());
        holder.Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PestDetails.class);
                intent.putExtra("Image", PestList.get(holder.getAdapterPosition()).getImageUrl());
                intent.putExtra("Identify", PestList.get(holder.getAdapterPosition()).getIdentify());
                intent.putExtra("Chemical", PestList.get(holder.getAdapterPosition()).getChemical());
                intent.putExtra("Damage", PestList.get(holder.getAdapterPosition()).getDamage());
                intent.putExtra("Physical", PestList.get(holder.getAdapterPosition()).getPhysical());
                intent.putExtra("Title", PestList.get(holder.getAdapterPosition()).getTitle());
                intent.putExtra("Key", PestList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView titleTextView;
        LinearLayout Item;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iconImage = itemView.findViewById(R.id.icon_image);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            Item = itemView.findViewById(R.id.pItem2);
        }
    }

}