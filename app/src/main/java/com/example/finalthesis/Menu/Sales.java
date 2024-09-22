package com.example.finalthesis.Menu;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.Marketplace.MarketData;
import com.example.finalthesis.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sales extends AppCompatActivity {

    Toolbar toolbar;
    private RecyclerView recyclerView;
    private SaleAdapter adapter;
    private List<MarketData> marketplaceItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Top Selling Products");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        marketplaceItems = new ArrayList<>();
        adapter = new SaleAdapter(marketplaceItems, this);
        recyclerView.setAdapter(adapter);
        fetchMarketplaceData();
    }

    private void fetchMarketplaceData() {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Marketplace");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                marketplaceItems.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    MarketData item = productSnapshot.getValue(MarketData.class);
                    if (item != null && item.getTotalItemOrders() > 0) {
                        marketplaceItems.add(item);
                    }
                }
                // Sort marketplaceItems based on totalItemOrders
                Collections.sort(marketplaceItems, new Comparator<MarketData>() {
                    @Override
                    public int compare(MarketData item1, MarketData item2) {
                        // Sort in descending order
                        return Integer.compare(item2.getTotalItemOrders(), item1.getTotalItemOrders());
                    }
                });
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Sales.this, "Error getting data from 'Marketplace' node: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
