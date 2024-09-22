package com.example.finalthesis.Menu;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.PestandDisease.PestData;
import com.example.finalthesis.PestandDisease.PestDetails;
import com.example.finalthesis.R;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    Context context;
    ArrayList<NewsData> newsData;

    public NewsAdapter(Context context, ArrayList<NewsData> newsData) {
        this.context = context;
        this.newsData = newsData;
    }


    @NonNull
    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news,parent,false);
        return new NewsAdapter.MyViewHolder(view);
    }

    @Override
    public int getItemCount() {

        return newsData.size();
    }

    public void searchDataList(ArrayList<NewsData> searchList){
        newsData = searchList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.MyViewHolder holder, int position) {
        NewsData currentNews = newsData.get(position);
        List<String> imageUrls = currentNews.getImageUrls();
        NewsImageAdapter imagesAdapter = new NewsImageAdapter(context, imageUrls);

        holder.images.setAdapter(imagesAdapter);
        holder.images.setLayoutManager(new GridLayoutManager(context, 3));
        holder.titleTextView.setText(currentNews.getTitle());
        holder.date.setText(currentNews.getUploadDate());
        holder.month.setText(currentNews.getUploadTime());
        holder.admin.setText(currentNews.getUsername());
        holder.descrip.setText(currentNews.getDescrip());


        int spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()); // Convert dp to pixels
        holder.images.addItemDecoration(new GridSpacingItemDecoration(3, spacing, true));
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        RecyclerView images;
        TextView titleTextView,date,month,admin,descrip;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            images = itemView.findViewById(R.id.images_recycler_view);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            date = itemView.findViewById(R.id.date);
            month = itemView.findViewById(R.id.month);
            admin = itemView.findViewById(R.id.admin);
            descrip = itemView.findViewById(R.id.description);
        }
    }

}