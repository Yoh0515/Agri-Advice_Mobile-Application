package com.example.finalthesis.PestandDisease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalthesis.PlantingGuidance.PlantAdapter6;
import com.example.finalthesis.PlantingGuidance.PlantData;
import com.example.finalthesis.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiseaseDetails extends AppCompatActivity {
    TextView pTitle, pIdentify, pDamage,pPhysical,pChemical;
    ImageView pImage;
    String key = "";
    String mainImage = "";
    Toolbar toolbar;
    RecyclerView recyclerView5;
    ArrayList<PlantData> PlantList;
    PlantAdapter6 plantAdapter6;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_details);

        pTitle = findViewById(R.id.pTitle);
        pIdentify = findViewById(R.id.identify);
        pDamage = findViewById(R.id.damage);
        pPhysical = findViewById(R.id.physical);
        pChemical = findViewById(R.id.chemical);
        pImage = findViewById(R.id.pImage);

        recyclerView5 = findViewById(R.id.PrecycleView);
        recyclerView5.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView5.setLayoutManager(layoutManager);
        PlantList = new ArrayList<>();
        plantAdapter6 = new PlantAdapter6(getApplicationContext(), PlantList);
        recyclerView5.setAdapter(plantAdapter6);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pTitle.setText(bundle.getString("Title"));
            pDamage.setText(bundle.getString("Damage"));
            pChemical.setText(bundle.getString("Chemical"));
            pPhysical.setText(bundle.getString("Physical"));
            pIdentify.setText(bundle.getString("Identify"));
            key = bundle.getString("Key");
            mainImage = bundle.getString("Image");
            Glide.with(this).load(mainImage).into(pImage);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(pTitle.getText().toString());


        databaseReference = FirebaseDatabase.getInstance().getReference("plants");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PlantList.clear();
                for (DataSnapshot plantSnapshot : snapshot.getChildren()) {
                    PlantData plantData = plantSnapshot.getValue(PlantData.class);
                    if (plantData != null) {
                        plantData.setKey(plantSnapshot.getKey());

                        DataSnapshot pestsSnapshot = plantSnapshot.child("disease");

                        for (DataSnapshot pestSnapshot : pestsSnapshot.getChildren()) {
                            String pestTitle = pestSnapshot.child("title").getValue(String.class);
                            Log.d("Pest Title", "Title: " + pestTitle);
                            if (pTitle.getText().toString().equals(pestTitle)){
                                PlantList.add(plantData);
                            }
                        }
                    }
                }
                plantAdapter6.notifyDataSetChanged();

                if (PlantList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No data available", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("FirebaseData", "Data retrieved: " + PlantList.size());
                }
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