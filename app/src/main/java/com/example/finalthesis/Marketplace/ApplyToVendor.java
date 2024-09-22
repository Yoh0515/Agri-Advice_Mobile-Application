package com.example.finalthesis.Marketplace;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalthesis.MainActivity;
import com.example.finalthesis.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ApplyToVendor extends AppCompatActivity {

    private static final int REQUEST_IMAGE_GALLERY = 101;

    private static final int REQUEST_IMAGE_GALLERY_FOR_G = 102;
    Toolbar toolbar;
    LinearLayout progressBar;
    TextInputEditText Username, Address, IdNumber, typeId;
    TextView idButton,isVendor;
    Button save;
    ImageView idView, gView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Uri selectedImageUri;
    String userId;
    RelativeLayout blured;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_to_vendor);

        blured = findViewById(R.id.blurID);
        save = findViewById(R.id.save);
        isVendor = findViewById(R.id.isVendor);
        Username = findViewById(R.id.namev);
        Address = findViewById(R.id.adddressv);
        IdNumber = findViewById(R.id.idNumv);
        progressBar =findViewById(R.id.progressbar);
        typeId = findViewById(R.id.typeId);
        idButton = findViewById(R.id.buttonSelectidImage);
        idView = findViewById(R.id.idImage);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Apply to Vendor");
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Address.setText(value.getString("Address"));
                Username.setText(value.getString("Fullname"));
            }
        });

        idButton.setOnClickListener(v -> openGallery());
        save.setOnClickListener(v -> uploadDataToFirebase());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            idView.setImageURI(selectedImageUri);
        } else if (requestCode == REQUEST_IMAGE_GALLERY_FOR_G && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            gView.setImageURI(selectedImageUri);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    private void uploadDataToFirebase() {
        if (selectedImageUri == null && typeId == null && IdNumber == null ) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String idNumber = IdNumber.getText().toString();
        String id = typeId.getText().toString();
        String vendorName = Username.getText().toString();
        String vendor = isVendor.getText().toString();

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        blured.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference idImageRef = storageReference.child("idImages").child(userId).child(System.currentTimeMillis() + ".jpg");

        Task<Uri> idImageTask = uploadImageToStorage(idImageRef);

        // Wait for both tasks to complete
        idImageTask.addOnSuccessListener(idImageUri -> {
            String idImageUrl = idImageUri.toString();

            Vendor vendor1 = new Vendor (idNumber, id, vendorName, vendor, idImageUrl);

            // Push the vendor data to the Firebase Realtime Database
            DatabaseReference vendorRef = FirebaseDatabase.getInstance().getReference().child("Vendors").child(userId);
            vendorRef.setValue(vendor1)
                    .addOnSuccessListener(aVoid -> {
                        // Hide progress bar
                        progressBar.setVisibility(View.INVISIBLE);
                        blured.setBackgroundColor(Color.TRANSPARENT);

                        // Data uploaded successfully
                        Toast.makeText(this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();

                        // Navigate back to the main activity
                        Intent intent = new Intent(this, MainActivity.class);
                        // If MainActivity expects extras or flags, you can pass them here
                        startActivity(intent);
                        finish(); // Close the current activity to prevent going back to it on back press
                    })
                    .addOnFailureListener(e -> {
                        // Hide progress bar
                        progressBar.setVisibility(View.INVISIBLE);
                        blured.setBackgroundColor(Color.TRANSPARENT);

                        // Failed to upload data
                        Toast.makeText(this, "Failed to upload data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }).addOnFailureListener(e -> {
            // Hide progress bar
            progressBar.setVisibility(View.INVISIBLE);
            blured.setBackgroundColor(Color.TRANSPARENT);

            // Failed to upload image
            Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    private Task<Uri> uploadImageToStorage(StorageReference imageRef) {
        return imageRef.putFile(selectedImageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                });
    }
}
