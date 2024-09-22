package com.example.finalthesis.Marketplace;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class ProductApproval extends AppCompatActivity{


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    RecyclerView recyclerView;
    TextView vname;
    ArrayList<MarketData> marketList;
    MarketAdapter_Approval marketAdapter_approval;
    FirebaseStorage storage;
    SearchView searchView;
    DatabaseReference databaseReference;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_approval);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product List");
        vname = findViewById(R.id.vname);
        recyclerView = findViewById(R.id.MrecycleView);
        databaseReference = FirebaseDatabase.getInstance().getReference("Marketplace");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        marketList = new ArrayList<>();
        marketAdapter_approval = new MarketAdapter_Approval(marketList, this);
        storage = FirebaseStorage.getInstance();
        recyclerView.setAdapter(marketAdapter_approval);
        searchView = findViewById(R.id.search2);
        searchView.clearFocus();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();


        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                vname.setText(value.getString("Fullname"));
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                marketList.clear();
                String vendor = vname.getText().toString();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    try {
                        MarketData marketData = dataSnapshot.getValue(MarketData.class);
                        if(marketData != null && vendor.equals(marketData.getVendor()) ){
                            marketData.setKey(dataSnapshot.getKey());
                            marketList.add(marketData);
                        }
                    }catch (DatabaseException e){
                        Log.e("FirebaseError", "Error converting data to DataClass", e);
                    }


                }
                marketAdapter_approval.notifyDataSetChanged();
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

    public void searchList(String text){
        ArrayList<MarketData> searchList = new ArrayList<>();
        for(MarketData dataClass: marketList){
            if(dataClass.getDescription().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        marketAdapter_approval.searchDataList(searchList);
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