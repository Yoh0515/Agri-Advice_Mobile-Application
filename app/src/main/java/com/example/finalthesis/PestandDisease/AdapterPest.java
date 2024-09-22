package com.example.finalthesis.PestandDisease;

import android.content.Context;
import android.content.Intent;
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

public class AdapterPest extends RecyclerView.Adapter<AdapterPest.MyViewHolder> {

    Context context;
    ArrayList<Data_Pest> PestList;

    public AdapterPest(Context context, ArrayList<Data_Pest> PestList) {
        this.context = context;
        this.PestList = PestList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {

        return PestList.size();
    }

    public void searchDataList(ArrayList<Data_Pest> searchList){
        PestList = searchList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Data_Pest pestData = PestList.get(position);
        holder.title.setText(pestData.getPlantTitle());
        Glide.with(context).load(pestData.getIconUrl()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsPest.class);
                intent.putExtra("Pest Title", PestList.get(holder.getAdapterPosition()).getPlantTitle());
                intent.putExtra("Key", PestList.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Description", PestList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("IconUrl", PestList.get(holder.getAdapterPosition()).getIconUrl());
                intent.putExtra("pdfUrl", PestList.get(holder.getAdapterPosition()).getPdfUrl());
                intent.putExtra("controlMeasures", PestList.get(holder.getAdapterPosition()).getControlMeasures());
                intent.putExtra("damageSymptoms", PestList.get(holder.getAdapterPosition()).getDamageSymptoms());
                intent.putExtra("insectCharacteristics", PestList.get(holder.getAdapterPosition()).getInsectCharacteristics());
                intent.putExtra("scientificName", PestList.get(holder.getAdapterPosition()).getScientificName());
                context.startActivity(intent);
            }
        });

    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);

        }
    }

}