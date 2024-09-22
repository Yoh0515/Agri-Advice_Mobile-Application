package com.example.finalthesis.Menu;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReportDetails extends AppCompatActivity {


    Toolbar toolbar;
    TextView subject, status, time, name, message;
    ImageView image;
    String key = "";
    String mainImage = "";
    RecyclerView recyclerView;
    ArrayList<ReportImageData> reportImageData;
    ReportImageAdapter reportImageAdapter;
    FirebaseFirestore firestore;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    String subjectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);


        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Report Details");

        subject = findViewById(R.id.rSubject);
        status = findViewById(R.id.rStatus);
        time = findViewById(R.id.rTime);
        message = findViewById(R.id.rMessage);
        name = findViewById(R.id.rName);
        image = findViewById(R.id.rImage);


        int numberOfColumns = 2;
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(ReportDetails.this, numberOfColumns));
        reportImageData = new ArrayList<>();
        reportImageAdapter = new ReportImageAdapter(this, reportImageData);
        recyclerView.setAdapter(reportImageAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            status.setText(bundle.getString("isResolved"));
            message.setText(bundle.getString("message"));
            name.setText(bundle.getString("senderName"));
            subjectText = bundle.getString("subject");
            subject.setText(subjectText);
            time.setText(bundle.getString("timestamp"));
            key = bundle.getString("Key");
            Glide.with(this).load(mainImage).into(image);

        }

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        String currentUserEmail = fAuth.getCurrentUser().getEmail();
        String adminEmail = "agriadvice123@gmail.com";
        String threadId = generateThreadId(currentUserEmail, adminEmail);

        fetchData(threadId, subjectText);

    }

    private void fetchData(String threadId, String subject) {
        firestore.collection("Complaints")
                .document(threadId)
                .collection("Chats")
                .whereEqualTo("subject", subject)  // Filter by subject
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        reportImageData.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.contains("images")) {
                                List<String> images = (List<String>) documentSnapshot.get("images");
                                if (images != null && !images.isEmpty()) {
                                    // Get the first image from the list
                                    String imageUrl = images.get(0);
                                    ReportImageData imageOnlyData = new ReportImageData();
                                    imageOnlyData.setImages(imageUrl); // Assuming ReportImageData has a setImageUrl method
                                    imageOnlyData.setKey(documentSnapshot.getId());
                                    reportImageData.add(imageOnlyData);
                                }
                            }
                        }
                        reportImageAdapter.notifyDataSetChanged();
                        Log.d("FirestoreData", "Data retrieved: " + reportImageData.size());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReportDetails.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String generateThreadId(String email1, String email2) {
        if (email1.compareTo(email2) < 0) {
            return email1 + "_" + email2;
        } else {
            return email2 + "_" + email1;
        }
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
