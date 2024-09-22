package com.example.finalthesis.Menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {

    private List<String> chatList;
    private OnChatClickListener onChatClickListener;
    private FirebaseFirestore fStore;
    private CollectionReference vendorsRef;

    public ChatsAdapter(List<String> chatList, OnChatClickListener onChatClickListener) {
        this.chatList = chatList;
        this.onChatClickListener = onChatClickListener;
        this.fStore = FirebaseFirestore.getInstance();
        this.vendorsRef = fStore.collection("Vendors");
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view, onChatClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        String chatEmail = chatList.get(position);
        holder.chatEmailTextView.setText(chatEmail);

        // Fetch vendor profile image
        vendorsRef.whereEqualTo("email", chatEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String profileImageUrl = document.getString("profileImage");
                        Glide.with(holder.chatImageView.getContext())
                                .load(profileImageUrl)
                                .placeholder(R.drawable.contact_icon)
                                .into(holder.chatImageView);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView chatEmailTextView;
        ImageView chatImageView;
        OnChatClickListener onChatClickListener;

        public ChatViewHolder(@NonNull View itemView, OnChatClickListener onChatClickListener) {
            super(itemView);
            chatEmailTextView = itemView.findViewById(R.id.chatEmailTextView);
            chatImageView = itemView.findViewById(R.id.chatImageView);
            this.onChatClickListener = onChatClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onChatClickListener.onChatClick(getAdapterPosition());
        }
    }

    public interface OnChatClickListener {
        void onChatClick(int position);
    }
}
