package com.example.finalthesis.Menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.Marketplace.Message;
import com.example.finalthesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessageList extends AppCompatActivity implements ChatsAdapter.OnChatClickListener {

    Toolbar toolbar;
    RecyclerView chatsRecyclerView;
    ChatsAdapter chatsAdapter;
    List<String> chatList;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagelist);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Message List");

        toolbar.setNavigationIcon(R.drawable.arrowback);
        int titleTextColor = getResources().getColor(android.R.color.white);
        toolbar.setTitleTextColor(titleTextColor);

        chatsRecyclerView = findViewById(R.id.chatsRecyclerView);
        chatList = new ArrayList<>();
        chatsAdapter = new ChatsAdapter(chatList, this);
        chatsRecyclerView.setAdapter(chatsAdapter);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userName = getIntent().getStringExtra("CURRENT_USER_NAME");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fetchChatList();
    }

    private void fetchChatList() {
        String currentUserEmail = fAuth.getCurrentUser().getEmail();

        fStore.collection("Messages")
                .whereArrayContains("participants", currentUserEmail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        chatList.clear();
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                List<String> participants = (List<String>) dc.getDocument().get("participants");
                                if (participants != null) {
                                    for (String email : participants) {
                                        if (!email.equals(currentUserEmail)) {
                                            chatList.add(email);
                                        }
                                    }
                                }
                            }
                        }
                        chatsAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onChatClick(int position) {
        String chatEmail = chatList.get(position);
        String currentUserEmail = fAuth.getCurrentUser().getEmail();
        String threadId = generateThreadId(currentUserEmail, chatEmail);

        Intent intent = new Intent(MessageList.this, Message.class);
        intent.putExtra("THREAD_ID", threadId);
        intent.putExtra("CURRENT_USER_EMAIL", currentUserEmail);
        intent.putExtra("VENDOR_EMAIL", chatEmail);
        intent.putExtra("CURRENT_USERNAME", userName);
        startActivity(intent);
    }

    private String generateThreadId(String email1, String email2) {
        if (email1.compareTo(email2) < 0) {
            return email1 + "_" + email2;
        } else {
            return email2 + "_" + email1;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
