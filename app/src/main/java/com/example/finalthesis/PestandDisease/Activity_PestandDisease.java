package com.example.finalthesis.PestandDisease;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Activity_PestandDisease extends AppCompatActivity {

    RecyclerView recyclerView, recyclerView2;
    SearchView searchView;
    DatabaseReference databaseReference, databaseReference2;

    AdapterPest pestAdapter;
    DiseaseAdapater diseaseAdapater;
    FirebaseFirestore firestore;
    CollectionReference pestCollection, diseaseCollection;
    ArrayList<DiseaseData> diseaseList;
    ArrayList<Data_Pest> pestList;
    Button bPests, bDisease;
    LinearLayout Lpest, Ldisease;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pestand_disease);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pest and Disease");

        firestore = FirebaseFirestore.getInstance();
        pestCollection = firestore.collection("Pest Pdfs");
        diseaseCollection = firestore.collection("Disease Pdfs");
        int numberOfColumns = 2;
        databaseReference = FirebaseDatabase.getInstance().getReference("Pests");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Disease");
        bPests = findViewById(R.id.bPests);
        bDisease = findViewById(R.id.bDisease);
        Lpest = findViewById(R.id.Lpests);
        Ldisease = findViewById(R.id.Ldisease);
        recyclerView = findViewById(R.id.recycleView0);
        recyclerView2 = findViewById(R.id.recycleView10);
        recyclerView.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView2.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        pestList = new ArrayList<>();
        diseaseList = new ArrayList<>();
        pestAdapter = new AdapterPest(this, pestList);
        diseaseAdapater = new DiseaseAdapater(this, diseaseList);
        recyclerView.setAdapter(pestAdapter);
        recyclerView2.setAdapter(diseaseAdapater);
        searchView = findViewById(R.id.search1);
        searchView.clearFocus();


//        pestCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                pestList.clear();
//                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    Data_Pest plantData = documentSnapshot.toObject(Data_Pest.class);
//                    if (plantData != null) {
//                        plantData.setKey(documentSnapshot.getId());
//                        pestList.add(plantData);
//                    }
//                }
//                pestAdapter.notifyDataSetChanged();
//                Log.d("FirestoreData", "Data retrieved: " + pestList.size());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(Activity_PestandDisease.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                pestList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Data_Pest pestData = dataSnapshot.getValue(Data_Pest.class);
//                    if (pestData != null) {
//                        pestData.setKey(dataSnapshot.getKey());
//                        pestList.add(pestData);
//                    }
//                }
//                pestAdapter.notifyDataSetChanged();
//
//                // Add log statement to check if data is being retrieved
//                Log.d("FirebaseData", "Data retrieved: " + pestList.size());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(Activity_PestandDisease.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                diseaseList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DiseaseData diseaseData = dataSnapshot.getValue(DiseaseData.class);
                    if (diseaseData != null) {
                        diseaseData.setKey(dataSnapshot.getKey());
                        diseaseList.add(diseaseData);
                    }
                }
                diseaseAdapater.notifyDataSetChanged();

                // Add log statement to check if data is being retrieved
                Log.d("FirebaseData", "Data retrieved: " + diseaseList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_PestandDisease.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        bPests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ldisease.setVisibility(View.GONE);
                Lpest.setVisibility(View.VISIBLE);
                bPests.setBackgroundColor(Color.parseColor("#014421"));
                bPests.setTypeface(null, Typeface.BOLD);
                bDisease.setBackgroundColor(ContextCompat.getColor(Activity_PestandDisease.this, R.color.green));
                bDisease.setTypeface(null, Typeface.NORMAL);
            }
        });

        bDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Lpest.setVisibility(View.GONE);
                Ldisease.setVisibility(View.VISIBLE);
                bDisease.setBackgroundColor(Color.parseColor("#014421"));
                bDisease.setTypeface(null, Typeface.BOLD);
                bPests.setBackgroundColor(ContextCompat.getColor(Activity_PestandDisease.this, R.color.green));
                bPests.setTypeface(null, Typeface.NORMAL);
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
                searchList1(newText);
                return true;
            }
        });

    }

    public void searchList(String text) {
        ArrayList<Data_Pest> searchList = new ArrayList<>();
        for (Data_Pest pestData : pestList) {
            if (pestData.getPlantTitle().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(pestData);
            }
        }
        pestAdapter.searchDataList(searchList);
    }

    public void searchList1(String text) {
        ArrayList<DiseaseData> searchList = new ArrayList<>();
        for (DiseaseData diseaseData : diseaseList) {
            if (diseaseData.getTitle().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(diseaseData);
            }
        }
        diseaseAdapater.searchDataList(searchList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar navigation back button click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // This will mimic the back button press functionality
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}