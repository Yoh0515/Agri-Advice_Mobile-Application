package com.example.finalthesis.PlantingGuidance;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.PestandDisease.PestData;
import com.example.finalthesis.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Plant_Detail extends AppCompatActivity {

    TextView pTitle, pDescrip, pCategory,pSpace,pDepth,pWater,pSun,pGrow,pSeed,pPlant,pFeed,pHarv,pStor;
    ImageView pImage;
    String key = "";
    DatabaseReference databaseReference, databaseReference2, databaseReference3;
    PlantAdapter3 plantAdapter;
    PlantAdapter4 plantAdapter2;
    ArrayList<PlantData2> PlantList;
    ArrayList<PlantData3> PlantList2;
    ArrayList<PestData> PestList;
    RecyclerView recyclerView, recyclerView2, recyclerView_image;
    Toolbar toolbar;
    ImageAdapter adapter;
    List<String> mImageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detail);

        pTitle = findViewById(R.id.pTitle);
        pCategory = findViewById(R.id.categoryPlants);
        pSpace = findViewById(R.id.space);
        pDepth = findViewById(R.id.dept);
        pWater = findViewById(R.id.water);
        pSun = findViewById(R.id.sun);
        pGrow = findViewById(R.id.grow);
        pDescrip = findViewById(R.id.pDescrip);
        pSeed = findViewById(R.id.seed);
        pPlant = findViewById(R.id.plant);
        pFeed = findViewById(R.id.feed);
        pHarv = findViewById(R.id.harv);
        pStor = findViewById(R.id.stor);

        recyclerView_image = findViewById(R.id.recycler_view);
        recyclerView_image.setLayoutManager(new LinearLayoutManager(this));


        mImageUrls = new ArrayList<>();
        recyclerView_image.setAdapter(adapter);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_image.setLayoutManager(layoutManager3);


        recyclerView2 = findViewById(R.id.recyView2);
        recyclerView2.setHasFixedSize(true);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(layoutManager2);
        PlantList2 = new ArrayList<>();
        plantAdapter2 = new PlantAdapter4(getApplicationContext(), PlantList2);
        recyclerView2.setAdapter(plantAdapter2);

        recyclerView = findViewById(R.id.recyView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        PlantList = new ArrayList<>();
        plantAdapter = new PlantAdapter3(getApplicationContext(), PlantList, PestList);
        recyclerView.setAdapter(plantAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pTitle.setText(bundle.getString("Title"));
            pDescrip.setText(bundle.getString("Description"));
            pCategory.setText(bundle.getString("Category"));
            pSpace.setText(bundle.getString("Range"));
            pDepth.setText(bundle.getString("Depth"));
            pWater.setText(bundle.getString("Water"));
            pSun.setText(bundle.getString("Sun"));
            pGrow.setText(bundle.getString("Temp"));
            pSeed.setText(bundle.getString("Grow"));
            pPlant.setText(bundle.getString("Plant"));
            pFeed.setText(bundle.getString("Feed"));
            pHarv.setText(bundle.getString("Harvest"));
            pStor.setText(bundle.getString("Storage"));
            key = bundle.getString("Key");
        }


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(pTitle.getText().toString());

        databaseReference3 = FirebaseDatabase.getInstance().getReference("plants").child(key).child("images");
        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mImageUrls.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String imageUrl = dataSnapshot.getValue(String.class);
                    if(imageUrl != null){
                        mImageUrls.add(imageUrl);
                    }
                }
                adapter.notifyDataSetChanged();

                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    int i = 0;
                    @Override
                    public void run() {
                        if (i < adapter.getItemCount()) {
                            recyclerView_image.smoothScrollToPosition(i++);
                        } else {
                            // Reset counter if it reaches the end
                            i = 0;
                        }
                        handler.postDelayed(this, 2300); // 2000 milliseconds = 2 seconds
                    }
                };
                handler.postDelayed(runnable, 2300); // Start the handler immediately
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





        databaseReference2 = FirebaseDatabase.getInstance().getReference("plants").child(key).child("disease");
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                PlantList2.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PlantData3 plantData = dataSnapshot.getValue(PlantData3.class);
                    if(plantData != null){
                        plantData.setKey(dataSnapshot.getKey());
                        PlantList2.add(plantData);
                    }
                }
                plantAdapter.notifyDataSetChanged();

                Log.d("FirebaseData", "Data retrieved: " + PlantList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("plants").child(key).child("pests");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                PlantList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PlantData2 plantData = dataSnapshot.getValue(PlantData2.class);
                    if(plantData != null){
                        plantData.setKey(dataSnapshot.getKey());
                        PlantList.add(plantData);
                    }
                }
                plantAdapter.notifyDataSetChanged();

                Log.d("FirebaseData", "Data retrieved: " + PlantList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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