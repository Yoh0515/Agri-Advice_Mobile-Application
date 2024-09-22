package com.example.finalthesis.Marketplace;

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
import com.example.finalthesis.PlantingGuidance.PDFViewActivity;
import com.example.finalthesis.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;

    private List<MessageModel> messagesList;
    private String currentUserEmail;
    private Context context;

    public MessagesAdapter(List<MessageModel> messagesList, String currentUserEmail, Context context) {
        this.messagesList = messagesList;
        this.currentUserEmail = currentUserEmail;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messagesList.get(position);
        if (message.getSender().equals(currentUserEmail)) {
            return VIEW_TYPE_SENDER;
        } else {
            return VIEW_TYPE_RECEIVER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messagesList.get(position);
        String formattedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(message.getTimestamp()));

        if (holder.getItemViewType() == VIEW_TYPE_SENDER) {
            SenderViewHolder senderHolder = (SenderViewHolder) holder;
            senderHolder.messageTextView.setText(message.getMessage());
            senderHolder.timestampTextView.setText(formattedTime);
            displayAttachments(message.getImages(), message.getPdfs(), senderHolder.imagesLayout, senderHolder.pdfsLayout);
        } else {
            ReceiverViewHolder receiverHolder = (ReceiverViewHolder) holder;
            receiverHolder.messageTextView.setText(message.getMessage());
            receiverHolder.timestampTextView.setText(formattedTime);
            displayAttachments(message.getImages(), message.getPdfs(), receiverHolder.imagesLayout, receiverHolder.pdfsLayout);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    private void displayAttachments(List<String> imageUrls, List<String> pdfUrls, LinearLayout imagesLayout, LinearLayout pdfsLayout) {
        imagesLayout.removeAllViews();
        pdfsLayout.removeAllViews();

        if (imageUrls != null) {
            for (String url : imageUrls) {
                ImageView imageView = new ImageView(imagesLayout.getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
                layoutParams.setMargins(8, 0, 8, 0);
                imageView.setLayoutParams(layoutParams);
                Glide.with(imagesLayout.getContext()).load(url).into(imageView);
                imagesLayout.addView(imageView);
            }
        }

        if (imageUrls != null) {
            for (String pdfUrl : imageUrls) {
                if (pdfUrl.contains(".pdf")) {
                    TextView pdfView = new TextView(pdfsLayout.getContext());
                    pdfView.setText("Click to view PDF");
                    pdfView.setTextColor(pdfsLayout.getContext().getResources().getColor(android.R.color.holo_blue_dark));
                    pdfView.setOnClickListener(v -> {
                        Intent intent = new Intent(pdfsLayout.getContext(), PDFViewActivity.class);
                        intent.putExtra("pdf_url", pdfUrl);
                        pdfsLayout.getContext().startActivity(intent);
                    });
                    pdfsLayout.addView(pdfView);
                }
            }
        }
    }
    static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampTextView;
        LinearLayout imagesLayout;
        LinearLayout pdfsLayout;

        SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            imagesLayout = itemView.findViewById(R.id.senderImagesLayout);
            pdfsLayout = itemView.findViewById(R.id.senderPdfsLayout);
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampTextView;
        LinearLayout imagesLayout;
        LinearLayout pdfsLayout;

        ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            imagesLayout = itemView.findViewById(R.id.receiverImagesLayout);
            pdfsLayout = itemView.findViewById(R.id.receiverPdfsLayout);
        }
    }
}
