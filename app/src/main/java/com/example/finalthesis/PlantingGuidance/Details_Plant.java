package com.example.finalthesis.PlantingGuidance;

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

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Details_Plant extends AppCompatActivity {

    TextView downloadPdf, pTitle, pDescrip, pCategory, plugarpagtatanim, ppagpilingbinhi, ppaghandanglupangtaniman, ppagpuntaatpagpalittanim, ppangangalaga, sakitpeste, ppagaani;
    FirebaseFirestore db;
    ImageView piconimage;
    String key = "";
    String mainImage = "";
    CollectionReference plantCollection;
    ArrayList<ImageData> PlantList;
    ArrayList<ImageTitle> PlantTitle;
    ImagePlantAdapter plantAdapter;
    RecyclerView recyclerView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_plant);

        db = FirebaseFirestore.getInstance();
        downloadPdf = findViewById(R.id.downloadPdf);
        pTitle = findViewById(R.id.ptitle);
        pDescrip = findViewById(R.id.pdescrip);
        pCategory = findViewById(R.id.pcateg);
        plugarpagtatanim = findViewById(R.id.plugarpagtatanim);
        ppagpilingbinhi = findViewById(R.id.ppagpilingbinhi);
        ppaghandanglupangtaniman = findViewById(R.id.ppaghandanglupangtaniman);
        ppagpuntaatpagpalittanim = findViewById(R.id.ppagpuntaatpagpalittanim);
        ppangangalaga = findViewById(R.id.ppangangalaga);
        sakitpeste = findViewById(R.id.sakitatpeste);
        ppagaani = findViewById(R.id.ppagaani);
        piconimage = findViewById(R.id.piconimage);

        plantCollection = db.collection("Plant Pdfs");
        recyclerView = findViewById(R.id.recycleViewPlant);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PlantList = new ArrayList<>();
        PlantTitle = new ArrayList<>();
        plantAdapter = new ImagePlantAdapter(this, PlantList, PlantTitle);
        recyclerView.setAdapter(plantAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pTitle.setText(bundle.getString("Title"));
            pCategory.setText(bundle.getString("Category"));
            pDescrip.setText(bundle.getString("Description"));
            sakitpeste.setText(bundle.getString("Sakit at Peste"));
            plugarpagtatanim.setText(bundle.getString("LT"));
            ppagpilingbinhi.setText(bundle.getString("Lipat Binhi"));
            ppagaani.setText(bundle.getString("Paggani"));
            ppaghandanglupangtaniman.setText(bundle.getString("LT"));
            ppagpuntaatpagpalittanim.setText(bundle.getString("Lipat"));
            ppangangalaga.setText(bundle.getString("Pagpapahalaga"));
            key = bundle.getString("Key");
            mainImage = bundle.getString("IconUrl");
            Glide.with(this).load(mainImage).into(piconimage);
        }

//        if (bundle.getString("Description") != null && !bundle.getString("Description").isEmpty()) {
//            pDescrip.setText(bundle.getString("Description"));
//            pDescrip.setBackgroundResource(R.drawable.alert2);
//        }

        if (bundle.getString("Sakit at Peste") != null && !bundle.getString("Sakit at Peste").isEmpty()) {
            sakitpeste.setText(bundle.getString("Sakit at Peste"));
            sakitpeste.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("LT") != null && !bundle.getString("LT").isEmpty()) {
            plugarpagtatanim.setText(bundle.getString("LT"));
            plugarpagtatanim.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("Lipat Binhi") != null && !bundle.getString("Lipat Binhi").isEmpty()) {
            ppagpilingbinhi.setText(bundle.getString("Lipat Binhi"));
            ppagpilingbinhi.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("Paggani") != null && !bundle.getString("Paggani").isEmpty()) {
            ppagaani.setText(bundle.getString("Paggani"));
            ppagaani.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("LT") != null && !bundle.getString("LT").isEmpty()) {
            ppaghandanglupangtaniman.setText(bundle.getString("LT"));
            ppaghandanglupangtaniman.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("Lipat") != null && !bundle.getString("Lipat").isEmpty()) {
            ppagpuntaatpagpalittanim.setText(bundle.getString("Lipat"));
            ppagpuntaatpagpalittanim.setBackgroundResource(R.drawable.alert2);
        }

        if (bundle.getString("Pagpapahalaga") != null && !bundle.getString("Pagpapahalaga").isEmpty()) {
            ppangangalaga.setText(bundle.getString("Pagpapahalaga"));
            ppangangalaga.setBackgroundResource(R.drawable.alert2);
        }



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // Fetch and display only the relevant pestDiseaseUrls and pestDiseaseTitles for the specific plant
        plantCollection.document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<String> pestDiseaseUrls = (List<String>) documentSnapshot.get("pestDiseaseUrls");
                    List<String> pestDiseaseTitles = (List<String>) documentSnapshot.get("pestDiseaseTitles");

                    if (pestDiseaseUrls != null && !pestDiseaseUrls.isEmpty() && pestDiseaseTitles != null && !pestDiseaseTitles.isEmpty()) {
                        for (int i = 0; i < pestDiseaseUrls.size(); i++) {
                            String url = pestDiseaseUrls.get(i);
                            String title = pestDiseaseTitles.get(i);

                            ImageData imageData = new ImageData(); // Assuming ImageData class exists
                            imageData.setKey(key); // Set the document key as ImageData key
                            imageData.setPestDiseaseUrls(url); // Assuming ImageData has field for imageUrl
                            PlantList.add(imageData);

                            ImageTitle imageTitle = new ImageTitle(); // Assuming ImageTitle class exists
                            imageTitle.setPestDiseaseTitles(title); // Assuming ImageTitle has field for title
                            PlantTitle.add(imageTitle);
                        }
                    }
                    plantAdapter.notifyDataSetChanged();
                    Log.d("FirestoreData", "Data retrieved: " + PlantList.size());
                } else {
                    Toast.makeText(Details_Plant.this, "Document not found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Details_Plant.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        downloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetchPdfUrlAndDownload(key);
            }
        });
    }

//    private void fetchPdfUrlAndDownload(String key) {
//        plantCollection.document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    String pdfUrl = documentSnapshot.getString("pdfUrl");
//                    if (pdfUrl != null) {
//                        startPdfDownload(pdfUrl);
//                    } else {
//                        Toast.makeText(Details_Plant.this, "PDF URL not available", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(Details_Plant.this, "Document not found", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(Details_Plant.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void startPdfDownload(String pdfUrl) {
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
