package com.example.finalthesis.PestandDisease;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class DetailsDisease extends AppCompatActivity {

    TextView downloadPdf, ptitle, scietificname, pdescrip, symptoms, ddevelopment, culcontrol, chemcontrol, biocontrol;
    FirebaseFirestore db;
    String key = "";
    String mainImage = "";
    CollectionReference diseaseCollection;
    ArrayList<ImageDiseaseData> diseaseList;
    ImageDiseaseAdapter diseaseAdapter;
    RecyclerView recyclerView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_disease);

        db = FirebaseFirestore.getInstance();
        downloadPdf = findViewById(R.id.downloadPdf);
        ptitle = findViewById(R.id.ptitle);
        scietificname = findViewById(R.id.scietificname);
        pdescrip = findViewById(R.id.pdescrip);
        symptoms = findViewById(R.id.symptoms);
        ddevelopment = findViewById(R.id.ddevelopment);
        culcontrol = findViewById(R.id.culcontrol);
        chemcontrol = findViewById(R.id.chemcontrol);
        biocontrol = findViewById(R.id.biocontrol);

        diseaseCollection = db.collection("Disease Pdfs");
        recyclerView = findViewById(R.id.recycleViewPlant);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        diseaseList = new ArrayList<>();
        diseaseAdapter = new ImageDiseaseAdapter(this, diseaseList);
        recyclerView.setAdapter(diseaseAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ptitle.setText(bundle.getString("Disease Title"));
            scietificname.setText(bundle.getString("scientificName"));
            pdescrip.setText(bundle.getString("Description"));
            symptoms.setText(bundle.getString("Symptoms"));
            ddevelopment.setText(bundle.getString("diseaseDevelopment"));
            culcontrol.setText(bundle.getString("culturalControl"));
            chemcontrol.setText(bundle.getString("chemicalControl"));
            biocontrol.setText(bundle.getString("biologicalControl"));
            key = bundle.getString("Key");
        }

        if (bundle.getString("scientificName") != null && !bundle.getString("scientificName").isEmpty()) {
            scietificname.setText(bundle.getString("scientificName"));
            scietificname.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("Symptoms") != null && !bundle.getString("Symptoms").isEmpty()) {
            symptoms.setText(bundle.getString("Symptoms"));
            symptoms.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("culturalControl") != null && !bundle.getString("culturalControl").isEmpty()) {
            culcontrol.setText(bundle.getString("culturalControl"));
            culcontrol.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("diseaseDevelopment") != null && !bundle.getString("diseaseDevelopment").isEmpty()) {
            ddevelopment.setText(bundle.getString("diseaseDevelopment"));
            ddevelopment.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("chemicalControl") != null && !bundle.getString("chemicalControl").isEmpty()) {
            chemcontrol.setText(bundle.getString("chemicalControl"));
            chemcontrol.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("biologicalControl") != null && !bundle.getString("biologicalControl").isEmpty()) {
            biocontrol.setText(bundle.getString("biologicalControl"));
            biocontrol.setBackgroundResource(R.drawable.alert2);
        }

        diseaseCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                diseaseList.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String key = documentSnapshot.getId();
                    List<String> diseaseImages = (List<String>) documentSnapshot.get("diseaseImages");

                    if (diseaseImages != null && !diseaseImages.isEmpty()) {
                        for (String url : diseaseImages) {
                            ImageDiseaseData imageData = new ImageDiseaseData(); // Assuming ImageData class exists
                            imageData.setKey(key); // Set the document key as ImageData key
                            imageData.setdiseaseImages(url); // Assuming ImageData has field for imageUrl
                            diseaseList.add(imageData);
                        }
                    }
                }
                diseaseAdapter.notifyDataSetChanged();
                Log.d("FirestoreData", "Data retrieved: " + diseaseList.size());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsDisease.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        downloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assuming you have a method to retrieve the PDF URL based on 'key'
                //fetchPdfUrlAndDownload(key);
            }
        });
    }

//    private void fetchPdfUrlAndDownload(String key) {
//        // Assuming you have a method to retrieve the PDF URL based on 'key' from Firebase Firestore
//        diseaseCollection.document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    String pdfUrl = documentSnapshot.getString("pdfUrl");
//                    if (pdfUrl != null) {
//                        // Initiate PDF download using the fetched URL
//                        startPdfDownload(pdfUrl);
//                    } else {
//                        Toast.makeText(DetailsDisease.this, "PDF URL not available", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(DetailsDisease.this, "Document not found", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(DetailsDisease.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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