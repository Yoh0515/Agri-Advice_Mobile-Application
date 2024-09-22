package com.example.finalthesis.Marketplace;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.finalthesis.MainActivity;
import com.example.finalthesis.R;
import com.google.android.material.textfield.TextInputEditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaymentConfirmation extends AppCompatActivity {
    private static final int REQUEST_IMAGE_GALLERY = 101;
    private static final int DELIVERY_FEE = 25;
    Button select, confirm, okay, buttoN;
    RelativeLayout alert;
    ScrollView qwerty;
    ImageView payImage;
    TextView remarks, vendorID;
    TextInputEditText id, name, amount, datepay, referenceNo;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Toolbar toolbar;
    Uri selectedImageUri;
    LinearLayout progressBar;
    DatabaseReference databaseReference, OrderRef;
    StorageReference storageReference;
    LinearLayout itemDetailsLayout, messageB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        vendorID = findViewById(R.id.vendorID);
        itemDetailsLayout = findViewById(R.id.itemDetailsLayout);
        messageB = findViewById(R.id.messageC);
        buttoN = findViewById(R.id.buttoN);
        databaseReference = FirebaseDatabase.getInstance().getReference("Payment");
        OrderRef = FirebaseDatabase.getInstance().getReference("Order");
        storageReference = FirebaseStorage.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payment Confirmation");
        id = findViewById(R.id.idUnique);
//        remarks = findViewById(R.id.remarks);
        name = findViewById(R.id.username);
        amount = findViewById(R.id.amount);
        okay = findViewById(R.id.okayP);
        qwerty = findViewById(R.id.qwert);
        datepay = findViewById(R.id.datepay);
        referenceNo = findViewById(R.id.referenceNo);
        payImage = findViewById(R.id.payImage);
        confirm = findViewById(R.id.button_confirm_payment);
        progressBar = findViewById(R.id.progressbar);
        select = findViewById(R.id.selectPay);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        String transactionId = intent.getStringExtra("transactionId");
        String totalPriceStr = intent.getStringExtra("totalPrice");
        String currentDate = intent.getStringExtra("currentDate");
        String vendor = intent.getStringExtra("vendor");
        ArrayList<CartItem> selectedItems = getIntent().getParcelableArrayListExtra("selectedItems");

        vendorID.setText(vendor);
        id.setText(transactionId);
        amount.setText(totalPriceStr);
        datepay.setText(currentDate);

        // Strip peso sign and convert to double
        String strippedTotalPriceStr = totalPriceStr.replace("₱", "").trim();
        double totalPrice = Double.parseDouble(strippedTotalPriceStr);
        double newTotalPrice = totalPrice + DELIVERY_FEE;
        amount.setText(String.format("%.2f", newTotalPrice));

        if (selectedItems != null) {
            for (CartItem item : selectedItems) {
                View itemView = getLayoutInflater().inflate(R.layout.item_checkout, null);

                ImageView itemImageView = itemView.findViewById(R.id.itemImage);
                TextView itemTitleTextView = itemView.findViewById(R.id.itemTitle);
                TextView itemPriceTextView = itemView.findViewById(R.id.itemPrice);
                TextView itemUnitTextView = itemView.findViewById(R.id.itemUnit);
                TextView itemStockTextView = itemView.findViewById(R.id.itemStock);
                TextView itemVendorTextView = itemView.findViewById(R.id.itemVendor);

                Glide.with(this).load(item.getImage().get(0)).into(itemImageView);
                itemTitleTextView.setText(item.getTitle());
                itemPriceTextView.setText("₱" + item.getPrice());
                itemUnitTextView.setText(item.getUnit());
                itemStockTextView.setText("Stocks: " + item.getQuantity());
                itemVendorTextView.setText("Vendor: " + item.getVendor());

                itemDetailsLayout.addView(itemView);
            }
        }

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("Fullname"));
            }
        });

        select.setOnClickListener(v -> openGallery());

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String transactionId = id.getText().toString().trim();
                String userName = name.getText().toString().trim();
                String totalPrice = amount.getText().toString().trim();
                String paymentDate = datepay.getText().toString().trim();
                String referenceNumber = referenceNo.getText().toString().trim();
                String vendor = vendorID.getText().toString().trim();

                if (referenceNumber.isEmpty()) {
                    Toast.makeText(PaymentConfirmation.this, "Please enter a reference number", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if (selectedImageUri == null) {
                    Toast.makeText(PaymentConfirmation.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                } else {
                    String imageName = UUID.randomUUID().toString();
                    final StorageReference imageReference = storageReference.child("images/" + imageName);

                    imageReference.putFile(selectedImageUri)
                            .addOnSuccessListener(taskSnapshot -> imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                qwerty.setBackgroundColor(Color.TRANSPARENT);
                                String imageUrl = uri.toString();

                                Map<String, Object> paymentData = new HashMap<>();
                                paymentData.put("transactionId", transactionId);
                                paymentData.put("userName", userName);
                                paymentData.put("totalPrice", String.format("%.2f", newTotalPrice));
                                paymentData.put("vendor", vendor);
                                paymentData.put("paymentDate", paymentDate);
                                paymentData.put("referenceNumber", referenceNumber);
                                paymentData.put("imageUrl", imageUrl);

                                databaseReference.child(transactionId).setValue(paymentData)
                                        .addOnSuccessListener(aVoid -> {
                                            if (selectedItems != null) {
                                                for (CartItem item : selectedItems) {
                                                    String itemId = shortenUUID(UUID.randomUUID().toString());
                                                    Map<String, Object> itemData = new HashMap<>();
                                                    itemData.put("title", item.getTitle());
                                                    itemData.put("price", item.getPrice());
                                                    itemData.put("unit", item.getUnit());
                                                    itemData.put("vendor", item.getVendor());
                                                    itemData.put("deliveryFee", "25");
                                                    itemData.put("itemQuantity", item.getCartCount());
                                                    itemData.put("image", item.getImage().get(0));
                                                    itemData.put("date", paymentDate);
                                                    itemData.put("buyer", userName);
                                                    databaseReference.child(transactionId).child("items").child(itemId).setValue(itemData);
                                                    OrderRef.child(itemId).setValue(itemData);

                                                    DatabaseReference plantsRef = FirebaseDatabase.getInstance().getReference("Marketplace");
                                                    plantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                                                                int totalQuantity = 0;
                                                                String title = plantSnapshot.child("title").getValue(String.class);
                                                                String quantityStr = plantSnapshot.child("quantity").getValue(String.class);
                                                                String key = plantSnapshot.getKey();
                                                                totalQuantity += item.getCartCount();
                                                                if (title != null && title.equals(item.getTitle())) {
                                                                    DatabaseReference plantRef = FirebaseDatabase.getInstance().getReference("Marketplace").child(key);
                                                                    int currentQuantity = Integer.parseInt(quantityStr);
                                                                    int updatedQuantity = currentQuantity - totalQuantity;

                                                                    String updatedStringQuantity = String.valueOf(updatedQuantity);// Decrement by the count of the item
                                                                    plantRef.child("quantity").setValue(updatedStringQuantity)
                                                                            .addOnSuccessListener(aVoid1 -> {
                                                                                //Toast.makeText(PaymentConfirmation.this, "Stock updated successfully", Toast.LENGTH_SHORT).show();
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                Log.e("Firebase", "Error: " + e.getMessage());
                                                                            });

                                                                    // Update totalItemOrders field
                                                                    int finalTotalQuantity = totalQuantity;
                                                                    plantRef.child("totalItemOrders").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            Integer currentTotalOrders = snapshot.getValue(Integer.class);
                                                                            if (currentTotalOrders == null) {
                                                                                currentTotalOrders = 0;
                                                                            }
                                                                            int updatedTotalOrders = currentTotalOrders + finalTotalQuantity;
                                                                            plantRef.child("totalItemOrders").setValue(updatedTotalOrders)
                                                                                    .addOnSuccessListener(aVoid1 -> {
                                                                                        //Toast.makeText(PaymentConfirmation.this, "Total orders updated successfully", Toast.LENGTH_SHORT).show();
                                                                                    })
                                                                                    .addOnFailureListener(e -> {
                                                                                        Log.e("Firebase", "Error updating totalItemOrders: " + e.getMessage());
                                                                                    });
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                            Log.e("Firebase", "Error: " + databaseError.getMessage());
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            Log.e("Firebase", "Error: " + databaseError.getMessage());
                                                        }
                                                    });

                                                    DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId).child(item.getKey());
                                                    cartRef.removeValue();
                                                }
                                            }
                                            messageB.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                            qwerty.setVisibility(View.GONE);
                                            toolbar.setVisibility(View.GONE);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(PaymentConfirmation.this, "Failed to upload payment confirmation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }))
                            .addOnFailureListener(e -> {
                                Toast.makeText(PaymentConfirmation.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });


        buttoN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentConfirmation.this, MainActivity.class);
                intent.putExtra("marketFragment", true);
                startActivity(intent);
            }
        });

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.setVisibility(View.GONE);
                qwerty.setBackgroundColor(Color.TRANSPARENT);
            }
        });

    }

    private String shortenUUID(String uuid) {
        return uuid.replaceAll("-", "").substring(0, 20);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            payImage.setImageURI(selectedImageUri);
        }
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
