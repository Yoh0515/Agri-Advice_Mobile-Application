package com.example.finalthesis.Marketplace;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class Order_Confirm extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyNotconfirm, recyConfirm;
    LinearLayout notconfirm, confirm;
    TextView textCon, textNotCon, vname;
    DatabaseReference databaseReference, databaseReferenceOrder;
    DatabaseReference databaseReference2;
    ArrayList<OrderData> orderData;
    ArrayList<OrderData2> orderData2;
    OrderAdapter orderAdapter;
    OrderAdapter2 orderAdapter2;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        vname = findViewById(R.id.nameV);
        textCon = findViewById(R.id.textCon);
        textNotCon = findViewById(R.id.textNotCon);
        notconfirm = findViewById(R.id.notConfirm);
        recyConfirm = findViewById(R.id.recyconfirm);
        recyNotconfirm = findViewById(R.id.recyNotconfirm);
        confirm = findViewById(R.id.Confirm);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order");

        databaseReference = FirebaseDatabase.getInstance().getReference("Payment");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Payment");
        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference("Order");
        recyNotconfirm.setHasFixedSize(true);
        recyNotconfirm.setLayoutManager(new LinearLayoutManager(this));
        orderData = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderData, this);
        recyNotconfirm.setAdapter(orderAdapter);


        recyConfirm.setHasFixedSize(true);
        recyConfirm.setLayoutManager(new LinearLayoutManager(this));
        orderData2 = new ArrayList<>();
        orderAdapter2 = new OrderAdapter2(orderData2, this);
        recyConfirm.setAdapter(orderAdapter2);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(executorService, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                vname.setText(value.getString("Fullname"));
            }
        });



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    orderData.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        OrderData orderData1 = dataSnapshot.getValue(OrderData.class);
                        if (orderData1 != null && vname.getText().toString().equals(orderData1.getVendor()) && orderData1.getIsConfirm() == null) {
                            orderData1.setKey(dataSnapshot.getKey());
                            orderData.add(orderData1);
                        }
                    }
                    // Sort the list by paymentDate before reversing
                    Collections.sort(orderData, new Comparator<OrderData>() {
                        @Override
                        public int compare(OrderData o1, OrderData o2) {
                            return o2.getPaymentDate().compareTo(o1.getPaymentDate()); // Sort in descending order
                        }
                    });
                    orderAdapter.notifyDataSetChanged();
                    Log.d("FirebaseData", "Data retrieved: " + orderData.size());
                } else {
                    Log.d("FirebaseData", "No data available");
                    // Handle case when no data is available
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Order_Confirm.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderData2.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OrderData2 orderData1 = dataSnapshot.getValue(OrderData2.class);
                    if (orderData1 != null && vname.getText().toString().equals(orderData1.getVendor()) && "true".equals(orderData1.getIsConfirm())) {
                        orderData1.setKey(dataSnapshot.getKey());
                        orderData2.add(orderData1);
                    }
                }
                Collections.sort(orderData2, new Comparator<OrderData2>() {
                    @Override
                    public int compare(OrderData2 o1, OrderData2 o2) {
                        return o2.getPaymentDate().compareTo(o1.getPaymentDate()); // Sort in descending order
                    }
                });
                orderAdapter2.notifyDataSetChanged();
                Log.d("FirebaseData", "Data retrieved: " + orderData2.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Order_Confirm.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyConfirm.setVisibility(View.VISIBLE);
                recyNotconfirm.setVisibility(View.GONE);
                confirm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#57A855")));
                notconfirm.setBackgroundResource(R.drawable.alert2);
                notconfirm.setBackgroundTintList(null);
                textCon.setTextColor(Color.WHITE);
                textNotCon.setTextColor(Color.parseColor("#57A855"));
            }
        });

        notconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyConfirm.setVisibility(View.GONE);
                recyNotconfirm.setVisibility(View.VISIBLE);
                notconfirm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#57A855")));
                confirm.setBackgroundTintList(null);
                confirm.setBackgroundResource(R.drawable.alert2);
                textNotCon.setTextColor(Color.WHITE);
                textCon.setTextColor(Color.parseColor("#57A855"));
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