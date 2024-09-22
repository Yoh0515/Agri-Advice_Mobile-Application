package com.example.finalthesis.Marketplace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<MarketData.Feedback> feedbackList;
    private Context context;

    public FeedbackAdapter(List<MarketData.Feedback> feedbackList, Context context) {
        this.feedbackList = feedbackList;
        this.context = context;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        MarketData.Feedback feedback = feedbackList.get(position);

        holder.reviewerName.setText(feedback.getReviewerName());
        holder.reviewDate.setText(feedback.getReviewDate());
        holder.feedbackText.setText(feedback.getFeedbackText());
        holder.ratingBar.setRating(feedback.getRating());

        List<String> feedbackImages = feedback.getFeedbackImages();
        holder.imageContainer.removeAllViews();

        if (feedbackImages != null && !feedbackImages.isEmpty()) {
            holder.imageContainer.setVisibility(View.VISIBLE);
            for (String imageUrl : feedbackImages) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        150,
                        150
                );
                params.setMargins(10, 10, 10, 10);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(context)
                        .load(imageUrl)
                        .into(imageView);
                holder.imageContainer.addView(imageView);
            }
        } else {
            holder.imageContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView reviewerName, reviewDate, feedbackText;
        RatingBar ratingBar;
        LinearLayout imageContainer;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewerName = itemView.findViewById(R.id.reviewerName);
            reviewDate = itemView.findViewById(R.id.reviewDate);
            feedbackText = itemView.findViewById(R.id.feedbackText);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imageContainer = itemView.findViewById(R.id.imageContainer);
        }
    }
}
