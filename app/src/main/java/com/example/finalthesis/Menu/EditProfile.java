package com.example.finalthesis.Menu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalthesis.MainActivity;
import com.example.finalthesis.Marketplace.Market_Add;
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
import com.google.firebase.storage.UploadTask;

public class EditProfile extends AppCompatActivity {
    Toolbar toolbar;
    TextInputEditText address, number,name,gender;
    Button sButton;
    LinearLayout progressBar;
    RelativeLayout blured;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ImageView Vimage;
    String userId;
    private Uri imageUri;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        progressBar = findViewById(R.id.progressbar);
        blured = findViewById(R.id.blured);
        Vimage = findViewById(R.id.Vimage);
        name = findViewById(R.id.editName);
        address = findViewById(R.id.editAddress);
        number = findViewById(R.id.editNumber);
        gender = findViewById(R.id.editgender);
        sButton = findViewById(R.id.saveButton);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("Fullname"));
                address.setText(value.getString("Address"));
                number.setText(value.getString("Number"));
                gender.setText(value.getString("Gender"));
            }
        });

        DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference().child("Vendors").child(userId);
        vendorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (context != null) {
                    if (dataSnapshot.exists()) {
                        String profileImageUrl = dataSnapshot.child("profileImage").getValue(String.class);
                        if (profileImageUrl != null) {
                            Glide.with(context).load(profileImageUrl).into(Vimage);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (context != null) {
                    Toast.makeText(context, "Error getting data from 'Vendors' node: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            Vimage.setImageURI(imageUri);
                        }else {
                            Toast.makeText(EditProfile.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Vimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });


        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String updatedAddress = address.getText().toString().trim();
                String updatedNumber = number.getText().toString().trim();
                String updatedGender = gender.getText().toString().trim();
                updateUserData(updatedAddress, updatedNumber,updatedGender);

                Intent intent = new Intent(EditProfile.this, MainActivity.class);
                intent.putExtra("menuFragment", true);
                startActivity(intent);
            }
        });


    }
    // Inside the updateUserData() method after updating other user information
    private void updateUserData(String updatedAddress, String updatedNumber, String updatedGender) {
        blured.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        DocumentReference userRef = fStore.collection("Users").document(userId);

        userRef.update("Address", updatedAddress, "Number", updatedNumber,"Gender",updatedGender)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadProfileImageToDatabase();
                        Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to upload profile image URL to Realtime Database
    private void uploadProfileImageToDatabase() {
        if (imageUri != null) {
            // Get the Firebase Storage reference
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            // Create a reference to the location where the image will be saved in Firebase Storage
            StorageReference imageRef = storageRef.child("profile_images").child(userId + ".jpg");

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL of the uploaded image
                            imageRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Image uploaded successfully, get the download URL
                                            String imageUrl = uri.toString();
                                            // Update the user's profile image URL in the Realtime Database
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            DatabaseReference profileImageRef = databaseReference.child("Vendors").child(userId).child("profileImage");
                                            profileImageRef.setValue(imageUrl)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Profile image URL updated successfully
                                                            Toast.makeText(EditProfile.this, "Profile image uploaded and updated successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Failed to update profile image URL
                                                            Toast.makeText(EditProfile.this, "Failed to update profile image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to get download URL of the uploaded image
                                            Toast.makeText(EditProfile.this, "Failed to get download URL of the uploaded image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to upload image to Firebase Storage
                            Toast.makeText(EditProfile.this, "Failed to upload profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(EditProfile.this, "No profile image selected", Toast.LENGTH_SHORT).show();
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

    private String  getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}