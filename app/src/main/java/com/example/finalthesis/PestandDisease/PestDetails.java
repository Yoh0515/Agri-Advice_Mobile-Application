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
import com.example.finalthesis.PlantingGuidance.PlantAdapter;
import com.example.finalthesis.PlantingGuidance.PlantAdapter3;
import com.example.finalthesis.PlantingGuidance.PlantAdapter4;
import com.example.finalthesis.PlantingGuidance.PlantAdapter5;
import com.example.finalthesis.PlantingGuidance.PlantData;
import com.example.finalthesis.PlantingGuidance.PlantData2;
import com.example.finalthesis.PlantingGuidance.PlantData3;
import com.example.finalthesis.PlantingGuidance.PlantData4;
import com.example.finalthesis.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PestDetails extends AppCompatActivity {

    TextView pTitle, pIdentify, pDamage,pPhysical,pChemical;
    ImageView pImage;
    String key = "";
    String mainImage = "";
    Toolbar toolbar;
    RecyclerView recyclerView5;
    ArrayList<PlantData> PlantList;
    PlantAdapter5 plantAdapter5;
    DatabaseReference databaseReference;
    String pestTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pest_details);

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
        plantAdapter5 = new PlantAdapter5(getApplicationContext(), PlantList);
        recyclerView5.setAdapter(plantAdapter5);

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

                        DataSnapshot pestsSnapshot = plantSnapshot.child("pests");
                        for (DataSnapshot pestSnapshot : pestsSnapshot.getChildren()) {
                            String pestTitle = pestSnapshot.child("title").getValue(String.class);
                            Log.d("Pest Title", "Title: " + pestTitle);
                            if (pTitle.getText().toString().equals(pestTitle)){
                                PlantList.add(plantData);
                            }
                        }
                    }
                }
                plantAdapter5.notifyDataSetChanged();

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