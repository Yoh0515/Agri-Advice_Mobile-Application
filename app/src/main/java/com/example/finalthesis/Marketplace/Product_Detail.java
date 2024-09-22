package com.example.finalthesis.Marketplace;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Product_Detail extends AppCompatActivity {

    LinearLayout mVendorView;
    ViewPager2 viewPager;
    ImageView mCart, mDetailsProfile;
    TextView feedback, mdetailPrice, mDetailTitle, mDetailQyt, mdetailDescription, mNames, mLocation, mUnit, numCart, mEmailUSer, mEmail, addedToCart;
    String key;
    List<Product> shoppingCart;
    Button addcart, messageNow;
    List<String> imageUrls = new ArrayList<>();
    Toolbar toolbar;
    DatabaseReference cartRef;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, getCurrentUserName;
    RecyclerView feedbackRecyclerView;
    FeedbackAdapter feedbackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mDetailsProfile = findViewById(R.id.mDetailsProfile);
        mEmailUSer = findViewById(R.id.mEmailUSer);
        messageNow = findViewById(R.id.buyNow);
        mDetailQyt = findViewById(R.id.mDetailQyt);
        mCart = findViewById(R.id.mCart);
        addcart = findViewById(R.id.buttonCart);
        addedToCart = findViewById(R.id.addedCart);
        numCart = findViewById(R.id.numCart);
        mUnit = findViewById(R.id.dUnit);
        viewPager = findViewById(R.id.viewPager);
        mdetailPrice = findViewById(R.id.mDetailPrice);
        mDetailTitle = findViewById(R.id.mDetailTitle);
        mdetailDescription = findViewById(R.id.mDetailDescription);
        mNames = findViewById(R.id.mDetailsName);
        mLocation = findViewById(R.id.mDetailsLocation);
        mVendorView = findViewById(R.id.vendorView);
        feedbackRecyclerView = findViewById(R.id.feedbackRecyclerView);
        shoppingCart = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        mEmail = findViewById(R.id.mDetailsEmail);

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    mEmailUSer.setText(value.getString("UserEmail"));
                }
            }
        });

        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);

        fetchCartCount();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mdetailDescription.setText(bundle.getString("Description"));
            mDetailTitle.setText(bundle.getString("Title"));
            mdetailPrice.setText(bundle.getString("Price"));
            mNames.setText(bundle.getString("Name"));
            mUnit.setText(bundle.getString("Unit"));
            mLocation.setText(bundle.getString("Location"));
            mDetailQyt.setText(bundle.getString("Qyt"));
            key = bundle.getString("Key");

            // Get the image URLs
            imageUrls = bundle.getStringArrayList("Image");

            ImageAdapterPDetail imageAdapter = new ImageAdapterPDetail(imageUrls, this);
            viewPager.setAdapter(imageAdapter);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mDetailTitle.getText().toString());

        DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference().child("Vendors");
        CollectionReference _usersCollectionReference = FirebaseFirestore.getInstance().collection("Users");

        vendorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot vendorSnapshot : dataSnapshot.getChildren()) {
                    String vendorId = vendorSnapshot.getKey();
                    String profileImageUrl = vendorSnapshot.child("profileImage").getValue(String.class);
                    String vendorName = vendorSnapshot.child("vendorName").getValue(String.class);

                    if (mNames.getText().toString().equals(vendorName)) {
                        assert vendorId != null;
                        String currentUserUid = fAuth.getCurrentUser().getUid();
                        DocumentReference getVendorsEmail = _usersCollectionReference.document(vendorId);
                        DocumentReference getUserName = _usersCollectionReference.document(currentUserUid);

                        getVendorsEmail.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String email = documentSnapshot.getString("UserEmail");
                                    if (email != null) {
                                        mEmail.setText(email);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Vendor email not found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed to get vendor email", Toast.LENGTH_SHORT).show();
                            }
                        });

                        getUserName.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String email = documentSnapshot.getString("UserEmail");
                                    String fullName = documentSnapshot.getString("Fullname");
                                    if (email != null) {
                                        getCurrentUserName = fullName;
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Vendor email not found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed to get vendor email", Toast.LENGTH_SHORT).show();
                            }
                        });

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

                feedbackAdapter = new FeedbackAdapter(feedbackList, Product_Detail.this);
                feedbackRecyclerView.setAdapter(feedbackAdapter);
                feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(Product_Detail.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Product_Detail.this, "Error getting feedback: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Check if the item already exists in the cart
        cartRef.orderByChild("title").equalTo(mDetailTitle.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Item already exists in the cart, update the cartCount
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String cartItemId = snapshot.getKey();
                        DatabaseReference itemRef = cartRef.child(cartItemId);
                        int currentCartCount = snapshot.child("cartCount").getValue(Integer.class);
                        int newCartCount = currentCartCount;

                        // Check if the new cart count exceeds the available quantity
                        if (newCartCount > 0) {
                            addcart.setVisibility(View.GONE);
                            addedToCart.setVisibility(View.VISIBLE);
                        } else {
                            addcart.setVisibility(View.VISIBLE);
                            addedToCart.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Product_Detail.this, "Failed to check cart", Toast.LENGTH_SHORT).show();
            }
        });

        mVendorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Product_Detail.this, VendorView.class);
                intent.putExtra("Names", mNames.getText().toString());
                startActivity(intent);
                finish();
            }
        });

        addcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToCart();
            }
        });

        mCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Product_Detail.this, AddToCart.class));
                finish();
            }
        });

        messageNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUserEmail = fAuth.getCurrentUser().getEmail();
                String fetchCurrentUserName = getCurrentUserName;
                String vendorEmail = mEmail.getText().toString();
                String vendorName = mNames.getText().toString();
                String threadId = generateThreadId(currentUserEmail, vendorEmail);

                Intent intent = new Intent(Product_Detail.this, Message.class);
                intent.putExtra("THREAD_ID", threadId);
                intent.putExtra("CURRENT_USER_EMAIL", currentUserEmail);
                intent.putExtra("VENDOR_EMAIL", vendorEmail);
                intent.putExtra("VENDOR_NAME", vendorName);
                intent.putExtra("USER_NAME", fetchCurrentUserName);
                startActivity(intent);
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

    private void fetchCartCount() {
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalCartCount = 0;

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Integer cartCount = itemSnapshot.child("cartCount").getValue(Integer.class);
                    if (cartCount != null) {
                        totalCartCount += cartCount;
                    }
                }

                if (totalCartCount > 0) {
                    numCart.setText(String.valueOf(totalCartCount));
                    numCart.setVisibility(View.VISIBLE);
                } else {
                    numCart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(Product_Detail.this, "Failed to fetch cart count", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        fetchCartCount();
    }

    private void uploadToCart() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);
        int availableQuantity = Integer.parseInt(mDetailQyt.getText().toString());

        // Check if the item already exists in the cart
        cartRef.orderByChild("title").equalTo(mDetailTitle.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Item already exists in the cart, update the cartCount
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String cartItemId = snapshot.getKey();
                        DatabaseReference itemRef = cartRef.child(cartItemId);
                        int currentCartCount = snapshot.child("cartCount").getValue(Integer.class);
                        int newCartCount = currentCartCount + 1;

                        // Check if the new cart count exceeds the available quantity
                        if (newCartCount > availableQuantity) {
                            Toast.makeText(Product_Detail.this, "Cannot add more, out of stock", Toast.LENGTH_SHORT).show();
                        } else {
                            itemRef.child("cartCount").setValue(newCartCount)
                                    .addOnSuccessListener(aVoid -> {
                                        fetchCartCount();
                                        //Toast.makeText(Product_Detail.this, "Updated cart quantity", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(Product_Detail.this, "Failed to update cart", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                } else {
                    // Item does not exist in the cart, create a new entry
                    if (availableQuantity < 1) {
                        Toast.makeText(Product_Detail.this, "Cannot add more, out of stock", Toast.LENGTH_SHORT).show();
                    } else {
                        String cartItemId = cartRef.push().getKey();

                        HashMap<String, Object> cartItem = new HashMap<>();
                        cartItem.put("title", mDetailTitle.getText().toString());
                        cartItem.put("description", mdetailDescription.getText().toString());
                        cartItem.put("price", mdetailPrice.getText().toString());
                        cartItem.put("quantity", mDetailQyt.getText().toString());
                        cartItem.put("unit", mUnit.getText().toString());
                        cartItem.put("vendor", mNames.getText().toString());
                        cartItem.put("image", imageUrls);
                        cartItem.put("cartCount", 1);  // Initialize cartCount to 1

                        cartRef.child(cartItemId).setValue(cartItem)
                                .addOnSuccessListener(aVoid -> {
                                    fetchCartCount();
                                    //Toast.makeText(Product_Detail.this, "Added to cart", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Product_Detail.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Product_Detail.this, "Failed to check cart", Toast.LENGTH_SHORT).show();
            }
        });
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

