package com.example.finalthesis.PestandDisease;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetailsPest extends AppCompatActivity {


    Toolbar toolbar;
    FirebaseFirestore db;
    String key = "";
    String mainImage = "";
    CollectionReference pestCollection;
    RecyclerView recyclerView;
    TextView PestTitle, ScientificName, IC, DamageSymptoms, ControlMeasures, Downloadpdf;
    ImageView piconimage;
    ArrayList<ImagePestData> PestList;
    ImagePestAdapter pestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_pest);

        db = FirebaseFirestore.getInstance();
        pestCollection = db.collection("Pest Pdfs");

        PestTitle = findViewById(R.id.ptitle);
        ScientificName = findViewById(R.id.scietificname);
        IC = findViewById(R.id.icharater);
        DamageSymptoms = findViewById(R.id.dsymptoms);
        ControlMeasures = findViewById(R.id.cmeasures);
        Downloadpdf = findViewById(R.id.downloadPdf);

        recyclerView = findViewById(R.id.recycleViewPlant);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PestList = new ArrayList<>();
        pestAdapter = new ImagePestAdapter(this, PestList);
        recyclerView.setAdapter(pestAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            PestTitle.setText(bundle.getString("Pest Title"));
            ScientificName.setText(bundle.getString("scientificName"));
            IC.setText(bundle.getString("insectCharacteristics"));
            DamageSymptoms.setText(bundle.getString("damageSymptoms"));
            ControlMeasures.setText(bundle.getString("controlMeasures"));
            key = bundle.getString("Key");
        }

        if (bundle.getString("insectCharacteristics") != null && !bundle.getString("insectCharacteristics").isEmpty()) {
            IC.setText(bundle.getString("insectCharacteristics"));
            IC.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("damageSymptoms") != null && !bundle.getString("damageSymptoms").isEmpty()) {
            DamageSymptoms.setText(bundle.getString("damageSymptoms"));
            DamageSymptoms.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("controlMeasures") != null && !bundle.getString("controlMeasures").isEmpty()) {
            ControlMeasures.setText(bundle.getString("controlMeasures"));
            ControlMeasures.setBackgroundResource(R.drawable.alert2);
        }


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        pestCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                PestList.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String key = documentSnapshot.getId();
                    List<String> pestDiseaseUrls = (List<String>) documentSnapshot.get("pestImages");

                    if (pestDiseaseUrls != null && !pestDiseaseUrls.isEmpty()) {
                        for (String url : pestDiseaseUrls) {
                            ImagePestData imageData = new ImagePestData(); // Assuming ImageData class exists
                            imageData.setKey(key); // Set the document key as ImageData key
                            imageData.setPestImages(url); // Assuming ImageData has field for imageUrl
                            PestList.add(imageData);
                        }
                    }
                }
                pestAdapter.notifyDataSetChanged();
                Log.d("FirestoreData", "Data retrieved: " + PestList.size());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsPest.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Downloadpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assuming you have a method to retrieve the PDF URL based on 'key'
                //fetchPdfUrlAndDownload(key);
            }
        });
    }

//    private void fetchPdfUrlAndDownload(String key) {
//        // Assuming you have a method to retrieve the PDF URL based on 'key' from Firebase Firestore
//        pestCollection.document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    String pdfUrl = documentSnapshot.getString("pdfUrl");
//                    if (pdfUrl != null) {
//                        // Initiate PDF download using the fetched URL
//                        startPdfDownload(pdfUrl);
//                    } else {
//                        Toast.makeText(DetailsPest.this, "PDF URL not available", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(DetailsPest.this, "Document not found", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(DetailsPest.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void startPdfDownload(String pdfUrl) {
//        // Here you can implement the logic to download the PDF file using the pdfUrl
//        // For simplicity, you can open the URL in a browser or use a download manager
//
//        // Example: Opening the URL in a browser
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
//        startActivity(intent);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}