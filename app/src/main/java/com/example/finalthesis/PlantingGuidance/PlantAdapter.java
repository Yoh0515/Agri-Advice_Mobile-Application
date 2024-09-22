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

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {
    private Context mContext;
    private List<PlantData> mPlantList;

    public PlantAdapter(Context context, List<PlantData> plantList) {
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
        PlantData plantData = mPlantList.get(position);
        holder.title.setText(plantData.getPlantTitle());
        Glide.with(mContext).load(plantData.getIconUrl()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PDFViewActivity.class);
                intent.putExtra("pdf_url", plantData.getPdfUrl());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlantList.size();
    }

    public void searchDataList(List<PlantData> searchList) {
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
