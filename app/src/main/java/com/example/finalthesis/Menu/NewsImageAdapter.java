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

public class NewsImageAdapter extends RecyclerView.Adapter<NewsImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<String> ImageUrls;

    public NewsImageAdapter(Context context, List<String> imageUrls) {
        mContext = context;
        ImageUrls = imageUrls;
    }

    @NonNull
    @Override
    public NewsImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image2, parent, false);
        return new NewsImageAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsImageAdapter.ImageViewHolder holder, int position) {
        String imageUrl = ImageUrls.get(position);
        Glide.with(mContext).load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return ImageUrls.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
