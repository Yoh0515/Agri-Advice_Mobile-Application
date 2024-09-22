package com.example.finalthesis.PlantingGuidance;

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

import java.util.List;

public class AdapterPlant extends RecyclerView.Adapter<AdapterPlant.PlantViewHolder> {
    private Context mContext;
    private List<DataPlant> mPlantList;

    public AdapterPlant(Context context, List<DataPlant> plantList) {
        mContext = context;
        mPlantList = plantList;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        DataPlant plantData = mPlantList.get(position);
        holder.title.setText(plantData.getPlantTitle());
        Glide.with(mContext).load(plantData.getIconUrl()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Details_Plant.class);
                intent.putExtra("IconUrl", plantData.getIconUrl());
                intent.putExtra("Category", plantData.getCategory());
                intent.putExtra("LT", plantData.getLupangTaniman());
                intent.putExtra("Paggani", plantData.getPagaani());
                intent.putExtra("Pagpapahalaga", plantData.getPangangalaga());
                intent.putExtra("Lipat Binhi", plantData.getPaglilipatNgBinhi());
                intent.putExtra("Lipat", plantData.getPaglilipatTanim());
                intent.putExtra("Title", plantData.getPlantTitle());
                intent.putExtra("Description", plantData.getDescription());
                intent.putExtra("Sakit at Peste", plantData.getSakitAtPeste());
                intent.putExtra("Key", plantData.getKey());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlantList.size();
    }

    public void searchDataList(List<DataPlant> searchList) {
        mPlantList = searchList;
        notifyDataSetChanged();
    }

    public class PlantViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);
        }
    }
}
