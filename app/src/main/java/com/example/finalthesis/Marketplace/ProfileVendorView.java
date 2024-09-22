package com.example.finalthesis.Marketplace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class ProfileVendorView extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    TextView VendorName, AddressP, emailP,NumberP, orderP, businessAddressP;
    ArrayList<MarketData> marketList;
    MarketAdapterVendorView marketAdapter;
    SearchView searchView;
    DatabaseReference databaseReference, paymentsRef ;
    RecyclerView recyclerView;
    ImageView Vimage;
    LinearLayout editProfile;
    TextView numCart;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_vendor_view);

        orderP = findViewById(R.id.mCart);
        numCart = findViewById(R.id.numCart);
        businessAddressP = findViewById(R.id.businessAddressP);
        AddressP = findViewById(R.id.AddressP);
        emailP = findViewById(R.id.emailP);
        NumberP = findViewById(R.id.NumberP);
        Vimage = findViewById(R.id.Vimage);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        databaseReference = FirebaseDatabase.getInstance().getReference("Marketplace");
        paymentsRef = FirebaseDatabase.getInstance().getReference("Payment");
        recyclerView = findViewById(R.id.recycleView);
        VendorName = findViewById(R.id.NameVendor);
        editProfile = findViewById(R.id.editProfile);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        int numberOfColumns = 2;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,numberOfColumns));
        marketList = new ArrayList<>();
        marketAdapter = new MarketAdapterVendorView(marketList, this);
        recyclerView.setAdapter(marketAdapter);
        searchView = findViewById(R.id.search1);
        searchView.clearFocus();

        fetchCartCount();

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                VendorName.setText(value.getString("Fullname"));
                AddressP.setText(value.getString("Address"));
                emailP.setText(value.getString("UserEmail"));
                NumberP.setText(value.getString("Number"));

            }
        });

        DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference().child("Vendors").child(userId);
        vendorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profileImageUrl = dataSnapshot.child("profileImage").getValue(String.class);
                    String getBusinessAddress = dataSnapshot.child("BusinessAddress").getValue(String.class);
                    Glide.with(getApplicationContext()).load(profileImageUrl).into(Vimage);
                    businessAddressP.setText(getBusinessAddress);
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
                Intent intent = new Intent(ProfileVendorView.this, EditProfileVendor.class);
                startActivity(intent);
            }
        });

        orderP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileVendorView.this, Order_Confirm.class);
                startActivity(intent);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                marketList.clear();
                String vendor = VendorName.getText().toString();
                String zero = "0";
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    try {
                        MarketData marketData = dataSnapshot.getValue(MarketData.class);
                        if (marketData != null && vendor.equals(marketData.getVendor()) && !zero.equals(marketData.getQuantity())){
                            marketData.setKey(dataSnapshot.getKey());
                            marketList.add(marketData);
                        }
                    }catch (DatabaseException e){
                        Log.e("FirebaseError", "Error converting data to DataClass", e);
                    }
                }
                marketAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCartCount();
    }

    private void fetchCartCount() {
        paymentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countItems = 0; // Reset count

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String vendor = snapshot.child("vendor").getValue(String.class);
                    String confirm = snapshot.child("isConfirm").getValue(String.class);
                    if (vendor != null && vendor.equals(VendorName.getText().toString()) && confirm == null) {
                        countItems++;
                    }
                }

                if (countItems > 0) {
                    numCart.setText(String.valueOf(countItems));
                    numCart.setVisibility(View.VISIBLE);
                } else {
                    numCart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.e("PaymentCount", "Error counting payments: " + databaseError.getMessage());
            }
        });
    }


    public void searchList(String text){
        ArrayList<MarketData> searchList = new ArrayList<>();
        for(MarketData dataClass: marketList){
            if(dataClass.getTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        marketAdapter.searchDataList(searchList);
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