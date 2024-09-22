package com.example.finalthesis.Marketplace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.finalthesis.MainActivity;
import com.example.finalthesis.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class EditProfileVendor extends AppCompatActivity {
    Toolbar toolbar;
    ImageView gcashImage;
    Button save;
    TextInputEditText GcashName, GcashNumber, Address, Number, BusinessAddress;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView selectImage;
    String userId;
    Uri selectedImageUri;
    LinearLayout progressbar;
    RelativeLayout blured;
    private static final int REQUEST_IMAGE_GALLERY = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_vendor);

        blured = findViewById(R.id.blured);
        progressbar = findViewById(R.id.progressbar);
        save = findViewById(R.id.save);
        GcashName = findViewById(R.id.gcashName);
        GcashNumber = findViewById(R.id.gcashNumber);
        BusinessAddress = findViewById(R.id.businessAddress);
        Address = findViewById(R.id.editAddress);
        Number = findViewById(R.id.editNumber);
        selectImage = findViewById(R.id.buttonSelectImage);
        gcashImage = findViewById(R.id.gcashImage);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Vendors").child(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Address.setText(value.getString("Address"));
                Number.setText(value.getString("Number"));
            }
        });

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String getGcashName = dataSnapshot.child("GcashName").getValue(String.class);
                    String getGcashNumber = dataSnapshot.child("GcashNumber").getValue(String.class);
                    String getGcashQr = dataSnapshot.child("GcashQR").getValue(String.class);
                    String getBusinessAddress = dataSnapshot.child("BusinessAddress").getValue(String.class);
                    GcashName.setText(getGcashName);
                    GcashNumber.setText(getGcashNumber);
                    BusinessAddress.setText(getBusinessAddress);

                    if (getGcashQr != null && !getGcashQr.isEmpty()) {
                        // Load the Gcash QR image using Glide
                        Glide.with(EditProfileVendor.this)
                                .load(getGcashQr)
                                .into(gcashImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        gcashImage.setOnClickListener(v -> openGallery());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar.setVisibility(View.VISIBLE);
                blured.setBackgroundColor(ContextCompat.getColor(EditProfileVendor.this, R.color.gray));
                String gcashName = GcashName.getText().toString().trim();
                String gcashNumber = GcashNumber.getText().toString().trim();
                String address = Address.getText().toString().trim();
                String number = Number.getText().toString().trim();
                String businessAddress = BusinessAddress.getText().toString().trim();

                // Convert the selected image URI to a byte array
                if (selectedImageUri != null) {
                    Bitmap bitmap = ((BitmapDrawable) gcashImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    // Upload the byte array to Firebase Storage
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference imageRef = storageRef.child("images/" + userId + ".jpg");
                    imageRef.putBytes(imageData)
                            .addOnSuccessListener(taskSnapshot -> {
                                // Image uploaded successfully, get the download URL
                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    // Update vendor's profile information and add image URL to the Realtime Database
                                    DatabaseReference vendorRef = FirebaseDatabase.getInstance().getReference().child("Vendors").child(userId);
                                    DocumentReference df = fStore.collection("Users").document(userId);

                                    HashMap<String, Object> updates = new HashMap<>();
                                    updates.put("GcashName", gcashName);
                                    updates.put("GcashNumber", gcashNumber);
                                    updates.put("GcashQR", uri.toString());
                                    updates.put("BusinessAddress", businessAddress);
                                    vendorRef.updateChildren(updates);

                                    HashMap<String, Object> updatesUsers = new HashMap<>();
                                    updates.put("Address", address);
                                    updates.put("Number", number);
                                    updates.put("BusinessAddress", businessAddress);
                                    df.update(updatesUsers);

                                    String updatedAddress = address;
                                    String updatedNumber = number;
                                    String updatedBusinessAddress = businessAddress;
                                    updateUserData(updatedAddress, updatedNumber, updatedBusinessAddress);

                                    progressbar.setVisibility(View.GONE);
                                    Toast.makeText(EditProfileVendor.this, "Vendor Profile updated successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EditProfileVendor.this, MainActivity.class);
                                    intent.putExtra("marketFragment", true);
                                    startActivity(intent);
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(EditProfileVendor.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // If no image is selected, update only the text fields
                    DatabaseReference vendorRef = FirebaseDatabase.getInstance().getReference().child("Vendors").child(userId);
                    DocumentReference df = fStore.collection("Users").document(userId);
                    HashMap<String, Object> updates = new HashMap<>();
                    updates.put("GcashName", gcashName);
                    updates.put("GcashNumber", gcashNumber);
                    updates.put("BusinessAddress", businessAddress);
                    vendorRef.updateChildren(updates);

                    HashMap<String, Object> updatesUsers = new HashMap<>();
                    updates.put("Address", address);
                    updates.put("Number", number);
                    updates.put("BusinessAddress", businessAddress);
                    df.update(updatesUsers);

                    String updatedAddress = address;
                    String updatedNumber = number;
                    String updatedBusinessAddress = businessAddress;
                    updateUserData(updatedAddress, updatedNumber, updatedBusinessAddress);

                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(EditProfileVendor.this, "Vendor Profile updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditProfileVendor.this, MainActivity.class);
                    intent.putExtra("marketFragment", true);
                    startActivity(intent);
                }
            }
        });



    }

    private void updateUserData(String updatedAddress, String updatedNumber, String updatedBusinessAddress) {
        blured.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        DocumentReference userRef = fStore.collection("Users").document(userId);

        userRef.update("Address", updatedAddress, "Number", updatedNumber, "BusinessAddress", updatedBusinessAddress )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfileVendor.this, "Vendor Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileVendor.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    // Handle the result of the gallery intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            gcashImage.setImageURI(selectedImageUri);

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