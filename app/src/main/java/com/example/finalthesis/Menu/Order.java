package com.example.finalthesis.Menu;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Order extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView,recyclerView2;
    ArrayList<OrderItem> items,items2;
    OrderItemAdapter orderItemAdapter;
    OrderItemAdapter2 orderItemAdapter2;
    DatabaseReference databaseReference, databaseReference2;
    TextView name;
    Button received, ongoing;
    LinearLayout display1,display2;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button bPests, bDisease;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order History");

        databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = new ArrayList<>();
        orderItemAdapter = new OrderItemAdapter(this, items);
        recyclerView.setAdapter(orderItemAdapter);

        databaseReference2 = FirebaseDatabase.getInstance().getReference("Order");
        recyclerView2 = findViewById(R.id.recycleView2);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        items2 = new ArrayList<>();
        orderItemAdapter2 = new OrderItemAdapter2(this, items2);
        recyclerView2.setAdapter(orderItemAdapter2);


//        display1 = findViewById(R.id.display1);
//        display2 = findViewById(R.id.display2);
        bPests = findViewById(R.id.bPests);
        bDisease = findViewById(R.id.bDisease);
//        received = findViewById(R.id.received);
//        ongoing = findViewById(R.id.ongoing);
        name = findViewById(R.id.name);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(executorService, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("Fullname"));
            }
        });

        bPests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView2.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                bPests.setBackgroundColor(Color.parseColor("#014421"));
                bPests.setTypeface(null, Typeface.BOLD);
                bDisease.setBackgroundColor(ContextCompat.getColor(Order.this, R.color.green));
                bDisease.setTypeface(null, Typeface.NORMAL);
            }
        });

        bDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.VISIBLE);
                bDisease.setBackgroundColor(Color.parseColor("#014421"));
                bDisease.setTypeface(null, Typeface.BOLD);
                bPests.setBackgroundColor(ContextCompat.getColor(Order.this, R.color.green));
                bPests.setTypeface(null, Typeface.NORMAL);
            }
        });

//        received.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                display1.setVisibility(View.GONE);
//                display2.setVisibility(View.VISIBLE);
//            }
//        });
//
//        ongoing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                display2.setVisibility(View.GONE);
//                display1.setVisibility(View.VISIBLE);
//            }
//        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                items.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OrderItem orderData = dataSnapshot.getValue(OrderItem.class);
                    String textTrue = "true";
                    if (orderData != null && name.getText().toString().equals(orderData.getBuyer()) && textTrue.equals(orderData.getIsConfirm())) {
                        if(!textTrue.equals(orderData.getRecieved())){
                            orderData.setKey(dataSnapshot.getKey());
                            items.add(orderData);
                        }
                    }
                }
                Collections.sort(items, new Comparator<OrderItem>() {
                    @Override
                    public int compare(OrderItem o1, OrderItem o2) {
                        return o2.getDate().compareTo(o1.getDate()); // Sort in descending order
                    }
                });
                orderItemAdapter.notifyDataSetChanged();



                Log.d("FirebaseData", "Data retrieved: " + items.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Order.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                items2.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OrderItem orderData = dataSnapshot.getValue(OrderItem.class);
                    String textTrue = "true";
                    if (orderData != null && name.getText().toString().equals(orderData.getBuyer()) && textTrue.equals(orderData.getIsConfirm())) {
                        if(textTrue.equals(orderData.getRecieved())){
                            orderData.setKey(dataSnapshot.getKey());
                            items2.add(orderData);
                        }
                    }
                }
                Collections.sort(items2, new Comparator<OrderItem>() {
                    @Override
                    public int compare(OrderItem o1, OrderItem o2) {
                        return o2.getDate().compareTo(o1.getDate()); // Sort in descending order
                    }
                });
                orderItemAdapter2.notifyDataSetChanged();



                Log.d("FirebaseData", "Data retrieved: " + items2.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Order.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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