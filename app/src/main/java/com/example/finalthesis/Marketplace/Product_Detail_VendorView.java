package com.example.finalthesis.Marketplace;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Product_Detail_VendorView extends AppCompatActivity {

    LinearLayout mVendorView;
    ImageView mDetailsProfile, minusButton, plusButton;
    TextView mDetailPrice, mDetailTitle, mDetailQyt, mDetailDescription, mNames, mLocation, mUnit;
    String key = "";
    List<Product> shoppingCart;
    Toolbar toolbar;
    RecyclerView feedbackRecyclerView;
    List<String> imageUrls = new ArrayList<>();
    FeedbackAdapter feedbackAdapter;

    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_vendor_view);

        mDetailsProfile = findViewById(R.id.mDetailsProfile);
        mUnit = findViewById(R.id.dUnit);
        mDetailPrice = findViewById(R.id.mDetailPrice);
        mDetailTitle = findViewById(R.id.mDetailTitle);
        mDetailDescription = findViewById(R.id.mDetailDescription);
        mNames = findViewById(R.id.mDetailsName);
        mLocation = findViewById(R.id.mDetailsLocation);
        mDetailQyt = findViewById(R.id.mDetailQyt);
        feedbackRecyclerView = findViewById(R.id.feedbackRecyclerView);
        mVendorView = findViewById(R.id.vendorView);
        minusButton = findViewById(R.id.minus_button);
        plusButton = findViewById(R.id.plus_button);
        shoppingCart = new ArrayList<>();
        viewPager = findViewById(R.id.viewPager);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mDetailDescription.setText(bundle.getString("Description"));
            mDetailTitle.setText(bundle.getString("Title"));
            mDetailPrice.setText(bundle.getString("Price"));
            mNames.setText(bundle.getString("Name"));
            mUnit.setText(bundle.getString("Unit"));
            mLocation.setText(bundle.getString("Location"));
            mDetailQyt.setText(bundle.getString("Qyt"));
            key = bundle.getString("Key");

            imageUrls = bundle.getStringArrayList("Image");

            ImageAdapterPDetail imageAdapter = new ImageAdapterPDetail(imageUrls, this);
            viewPager.setAdapter(imageAdapter);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mDetailTitle.getText().toString());

        DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference().child("Vendors");
        vendorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot vendorSnapshot : dataSnapshot.getChildren()) {
                    String vendorId = vendorSnapshot.getKey();
                    String profileImageUrl = vendorSnapshot.child("profileImage").getValue(String.class);
                    String vendorName = vendorSnapshot.child("vendorName").getValue(String.class);

                    if (mNames.getText().toString().equals(vendorName)) {
                        Glide.with(getApplicationContext()).load(profileImageUrl).into(mDetailsProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error getting data from 'Vendors' node: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Marketplace");
        productRef.child(key).child("feedbackArray").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<MarketData.Feedback> feedbackList = new ArrayList<>();

                for (DataSnapshot feedbackSnapshot : dataSnapshot.getChildren()) {
                    MarketData.Feedback feedback = feedbackSnapshot.getValue(MarketData.Feedback.class);
                    if (feedback != null) {
                        feedbackList.add(feedback);
                    }
                }

                feedbackAdapter = new FeedbackAdapter(feedbackList, Product_Detail_VendorView.this);
                feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(Product_Detail_VendorView.this));
                feedbackRecyclerView.setAdapter(feedbackAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Product_Detail_VendorView.this, "Error getting feedback: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mVendorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Product_Detail_VendorView.this, VendorView.class);
                intent.putExtra("Names", mNames.getText().toString());
                startActivity(intent);
                finish();
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStockUpdateDialog(false);
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStockUpdateDialog(true);
            }
        });
    }

    private void showStockUpdateDialog(boolean isAdding) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_stock_update, null);
        builder.setView(dialogView);

        EditText stockInput = dialogView.findViewById(R.id.stockInput);

        builder.setTitle(isAdding ? "Add Stocks" : "Subtract Stocks");
        builder.setPositiveButton(isAdding ? "Add" : "Subtract", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int quantity = Integer.parseInt(mDetailQyt.getText().toString());
                int stockChange = Integer.parseInt(stockInput.getText().toString());

                if (isAdding) {
                    quantity += stockChange;
                } else {
                    if (stockChange > quantity) {
                        Toast.makeText(Product_Detail_VendorView.this, "Quantity being deducted is greater than the stocks", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    quantity -= stockChange;
                }
                updateQuantityInFirebase(quantity);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void updateQuantityInFirebase(int quantity) {
        mDetailQyt.setText(String.valueOf(quantity));
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Marketplace").child(key);
        productRef.child("quantity").setValue(String.valueOf(quantity));
    }

}
