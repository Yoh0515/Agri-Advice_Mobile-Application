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
//import com.example.finalthesis.PlantingGuidance.PDFViewActivity;
import com.example.finalthesis.R;

import java.util.ArrayList;

public class DiseaseAdapater extends RecyclerView.Adapter<DiseaseAdapater.MyViewHolder> {

    Context context;
    ArrayList<DiseaseData> DiseaseList;

    public DiseaseAdapater(Context context, ArrayList<DiseaseData> DiseaseList) {
        this.context = context;
        this.DiseaseList = DiseaseList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {

        return DiseaseList.size();
    }

    public void searchDataList(ArrayList<DiseaseData> searchList){
        DiseaseList = searchList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DiseaseData diseaseData = DiseaseList.get(position);
        holder.title.setText(diseaseData.getPlantTitle());
        Glide.with(context).load(diseaseData.getIconUrl()).into(holder.imageView);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, PDFViewActivity.class);
//                intent.putExtra("pdf_url", diseaseData.getPdfUrl());
//                context.startActivity(intent);
//            }
//        });

//        Glide.with(context).load(DiseaseList.get(position).getImageUrl()).into(holder.iconImage);
//        holder.titleTextView.setText(DiseaseList.get(position).getTitle());
//        holder.Item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, DiseaseDetails.class);
//                intent.putExtra("Image", DiseaseList.get(holder.getAdapterPosition()).getImageUrl());
//                intent.putExtra("Identify", DiseaseList.get(holder.getAdapterPosition()).getIdentify());
//                intent.putExtra("Chemical", DiseaseList.get(holder.getAdapterPosition()).getChemical());
//                intent.putExtra("Damage", DiseaseList.get(holder.getAdapterPosition()).getDamage());
//                intent.putExtra("Physical", DiseaseList.get(holder.getAdapterPosition()).getPhysical());
//                intent.putExtra("Title", DiseaseList.get(holder.getAdapterPosition()).getTitle());
//                intent.putExtra("Key", DiseaseList.get(holder.getAdapterPosition()).getKey());
//                context.startActivity(intent);
//            }
//        });
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
//        ImageView iconImage;
//        TextView titleTextView;
//        LinearLayout Item;
        ImageView imageView;
        TextView title;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);

//            iconImage = itemView.findViewById(R.id.icon_image);
//            titleTextView = itemView.findViewById(R.id.title_text_view);
//            Item = itemView.findViewById(R.id.pItem2);
        }
    }

}