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

public class Seminar_Report_Module extends AppCompatActivity {

    RecyclerView recycleView;
    SeminarAdapter seminarAdapter;
    FirebaseFirestore firestore;
    CollectionReference SeminarCollection;
    ArrayList<SeminarData> SeminarList;
    Toolbar toolbar;
    Button addSeminar;
    ImageView messageSeminar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    String getCurrentUserName, getCurrentAddress, getCurrentPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seminar_report_module);

        firestore = FirebaseFirestore.getInstance();
        SeminarCollection = firestore.collection("Seminar");
        recycleView = findViewById(R.id.recycleView);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        SeminarList = new ArrayList<>();
        seminarAdapter = new SeminarAdapter(this, SeminarList);
        recycleView.setAdapter(seminarAdapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Request for Seminar");

        addSeminar = findViewById(R.id.addSeminar);
        messageSeminar = findViewById(R.id.messageSeminar);

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
                        Toast.makeText(Seminar_Report_Module.this, "Vendor email not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Seminar_Report_Module.this, "Failed to get vendor email", Toast.LENGTH_SHORT).show();
            }
        });

        String currentUserEmail = fAuth.getCurrentUser().getEmail();
        String adminEmail = "agriadvice123@gmail.com";
        String threadId = generateThreadId(currentUserEmail, adminEmail);

        fetchData(threadId);

        addSeminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentUserEmail = fAuth.getCurrentUser().getEmail();
                String adminEmail = "agriadvice123@gmail.com";
                String threadId = generateThreadId(currentUserEmail, adminEmail);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("Seminar").document(threadId);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Intent intent;
                            // Thread ID does not exist, go to SeminarForms
                            intent = new Intent(Seminar_Report_Module.this, com.example.finalthesis.Marketplace.SeminarForms.class);

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

        messageSeminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUserEmail = fAuth.getCurrentUser().getEmail();
                String adminEmail = "agriadvice123@gmail.com";
                String threadId = generateThreadId(currentUserEmail, adminEmail);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("Seminar").document(threadId);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Intent intent;
                            // Thread ID does not exist, go to SeminarForms
                            intent = new Intent(Seminar_Report_Module.this, com.example.finalthesis.Marketplace.MessagesAdminSeminar.class);

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

    }

    private void fetchData(String threadId) {
        firestore.collection("Seminar")
                .document(threadId)
                .collection("Chats")
                .whereEqualTo("message", "Request for Seminar - Pending")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        SeminarList.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            SeminarData seminarData = documentSnapshot.toObject(SeminarData.class);
                            if (seminarData != null) {
                                seminarData.setKey(documentSnapshot.getId());
                                SeminarList.add(seminarData);
                            }
                        }
                        seminarAdapter.notifyDataSetChanged();
                        Log.d("FirestoreData", "Data retrieved: " + SeminarList.size());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Seminar_Report_Module.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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