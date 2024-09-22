package com.example.finalthesis.Menu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Reports_Module extends AppCompatActivity {

    RecyclerView recycleView;
    FirebaseFirestore firestore;
    CollectionReference reportCollection;
    ReportAdapter reportAdapter;
    ArrayList<ReportData> ReportList;
    Toolbar toolbar;
    Button addReport;
    ImageView messageReport;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    String getCurrentUserName, getCurrentAddress, getCurrentPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_module);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Report");

        firestore = FirebaseFirestore.getInstance();
        reportCollection = firestore.collection("Complaints");
        recycleView = findViewById(R.id.recycleView);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ReportList = new ArrayList<>();
        reportAdapter = new ReportAdapter(this, ReportList);
        recycleView.setAdapter(reportAdapter);

        addReport = findViewById(R.id.addReport);
        messageReport = findViewById(R.id.messageReport);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        CollectionReference _usersCollectionInfoReference = FirebaseFirestore.getInstance().collection("Users");
        String currentUserUid = fAuth.getCurrentUser().getUid();
        DocumentReference getUserName = _usersCollectionInfoReference.document(currentUserUid);

        getUserName.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String email = documentSnapshot.getString("UserEmail");
                    String fullName = documentSnapshot.getString("Fullname");
                    String Address = documentSnapshot.getString("Address");
                    String PhoneNumber = documentSnapshot.getString("Number");
                    if (email != null) {
                        getCurrentUserName = fullName;
                        getCurrentAddress = Address;
                        getCurrentPhoneNumber = PhoneNumber;
                    } else {
                        Toast.makeText(Reports_Module.this, "Vendor email not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Reports_Module.this, "Failed to get vendor email", Toast.LENGTH_SHORT).show();
            }
        });

        addReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Reports_Module.this, ReportForm.class);
                startActivity(intent);
            }
        });

        messageReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentUserEmail = fAuth.getCurrentUser().getEmail();
                String adminEmail = "agriadvice123@gmail.com";
                String threadId = generateThreadId(currentUserEmail, adminEmail);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("Complaints").document(threadId);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Intent intent;
                            // Thread ID does not exist, go to SeminarForms
                            intent = new Intent(Reports_Module.this, com.example.finalthesis.Marketplace.MessageAdmin.class);

                            intent.putExtra("THREAD_ID", threadId);
                            intent.putExtra("CURRENT_USER_EMAIL", currentUserEmail);
                            intent.putExtra("ADMIN_NAME", "Agri Advice");
                            intent.putExtra("CURRENT_USER_NAME", getCurrentUserName);
                            intent.putExtra("CURRENT_USER_ADDRESS", getCurrentAddress);
                            intent.putExtra("CURRENT_USER_NUMBER", getCurrentPhoneNumber);
                            intent.putExtra("ADMIN_EMAIL", adminEmail);
                            startActivity(intent);
                        } else {
                            Log.d("Firestore", "Error getting document: ", task.getException());
                        }
                    }
                });
            }
        });


        String currentUserEmail = fAuth.getCurrentUser().getEmail();
        String adminEmail = "agriadvice123@gmail.com";
        String threadId = generateThreadId(currentUserEmail, adminEmail);

        fetchData(threadId);
    }

    private void fetchData(String threadId) {
        firestore.collection("Complaints")
                .document(threadId)
                .collection("Chats")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ReportList.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.contains("subject")) {
                                ReportData reportData = documentSnapshot.toObject(ReportData.class);
                                if (reportData != null) {
                                    reportData.setKey(documentSnapshot.getId());
                                    ReportList.add(reportData);
                                }
                            }
                        }
                        reportAdapter.notifyDataSetChanged();
                        Log.d("FirestoreData", "Data retrieved: " + ReportList.size());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Reports_Module.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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