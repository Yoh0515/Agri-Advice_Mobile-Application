package com.example.finalthesis.Marketplace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VendorView extends AppCompatActivity {

    TextView Vname;
    String key = "";
    ImageView Vimage, Vimage2;
    RecyclerView recyclerView;
    ArrayList<MarketData> marketList;
    MarketAdapter marketAdapter;
    SearchView searchView;
    DatabaseReference databaseReference;
    Toolbar toolbar;
    RelativeLayout viewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_view);

        Vimage2 = findViewById(R.id.Vimage2);
        viewImage  = findViewById(R.id.viewImage);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vendor Profile");
        databaseReference = FirebaseDatabase.getInstance().getReference("Marketplace");
        Vimage = findViewById(R.id.Vimage);
        Vname = findViewById(R.id.NameVendor);
        recyclerView = findViewById(R.id.recycleView);
        searchView = findViewById(R.id.search1);
        searchView.clearFocus();
        int numberOfColumns = 2;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,numberOfColumns));
        marketList = new ArrayList<>();
        marketAdapter = new MarketAdapter(marketList, this);
        recyclerView.setAdapter(marketAdapter);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Vname.setText(bundle.getString("Name"));
            key = bundle.getString("Key");
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("Names")) {
            String namesValue = intent.getStringExtra("Names");
            Vname.setText(namesValue);
        }

        Vimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewImage.setVisibility(View.VISIBLE);
            }
        });

        viewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewImage.setVisibility(View.GONE);
            }
        });

        DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference().child("Vendors");
        vendorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot vendorSnapshot : dataSnapshot.getChildren()) {
                    String vendorId = vendorSnapshot.getKey();
                    String profileImageUrl = vendorSnapshot.child("profileImage").getValue(String.class);
                    String vendorName = vendorSnapshot.child("vendorName").getValue(String.class);

                    if (Vname.getText().toString().equals(vendorName)) {
                        Glide.with(getApplicationContext()).load(profileImageUrl).into(Vimage);
                        Glide.with(getApplicationContext()).load(profileImageUrl).into(Vimage2);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error getting data from 'Vendors' node: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                marketList.clear();
                String vendor = Vname.getText().toString();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    try {
                        MarketData marketData = dataSnapshot.getValue(MarketData.class);
                        if(marketData != null && vendor.equals(marketData.getVendor()) ){
                            marketData.setKey(dataSnapshot.getKey());
                            marketList.add(marketData);
                        }
                    }catch (DatabaseException e){
                        Log.e("FirebaseError", "Error converting data to DataClass", e);
                    }


                }
                marketAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }

    public void searchList(String text){
        ArrayList<MarketData> searchList = new ArrayList<>();
        for(MarketData dataClass: marketList){
            if(dataClass.getDescription().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        marketAdapter.searchDataList(searchList);
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