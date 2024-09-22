package com.example.finalthesis.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileView extends AppCompatActivity {


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    TextView ProfileName, AddressP, GenderP,NumberP;
    ImageView Vimage;
    LinearLayout editProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        AddressP = findViewById(R.id.AddressP);
        GenderP = findViewById(R.id.GenderP);
        NumberP = findViewById(R.id.NumberP);
        Vimage = findViewById(R.id.Vimage);
        editProfile = findViewById(R.id.editProfile);
        ProfileName = findViewById(R.id.NameProfile);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ProfileName.setText(value.getString("Fullname"));
                AddressP.setText(value.getString("Address"));
                GenderP.setText(value.getString("Gender"));
                NumberP.setText(value.getString("Number"));
            }
        });

        DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference().child("Vendors").child(userId);
        vendorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profileImageUrl = dataSnapshot.child("profileImage").getValue(String.class);
                    Glide.with(getApplicationContext()).load(profileImageUrl).into(Vimage);
                } else {
                    Toast.makeText(getApplicationContext(), "No data found in 'Vendors' node for the current user", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error getting data from 'Vendors' node: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileView.this, EditProfile.class);
                startActivity(intent);
            }
        });
    }
}