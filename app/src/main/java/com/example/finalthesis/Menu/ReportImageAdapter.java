package com.example.finalthesis.Menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;

import java.util.List;

public class ReportImageAdapter extends RecyclerView.Adapter<ReportImageAdapter.PlantViewHolder> {
    private Context mContext;
    private List<ReportImageData> mPlantList;

    public ReportImageAdapter(Context context, List<ReportImageData> plantList) {
        mContext = context;
        mPlantList = plantList;
    }

    @NonNull
    @Override
    public ReportImageAdapter.PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_imagereport, parent, false);
        return new ReportImageAdapter.PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportImageAdapter.PlantViewHolder holder, int position) {

        ReportImageData plantData = mPlantList.get(position);
        Glide.with(mContext).load(plantData.getImages()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mPlantList.size();
    }



    public class PlantViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}

