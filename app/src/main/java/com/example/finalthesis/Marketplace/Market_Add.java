package com.example.finalthesis.Marketplace;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalthesis.MainActivity;
import com.example.finalthesis.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Market_Add extends AppCompatActivity {

    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private ViewPager2 viewPager;
    private View viewPagerOverlay;
    private List<Uri> selectedImages;
    private static final int MAX_IMAGE_COUNT = 5;
    private ImageAdapter imageAdapter;
    TextInputEditText mTitle, mPrice, mDescription, mQuantity, mStoreLocation, mVendorDB, intApproved ,businessAdd;
    AutoCompleteTextView mCategory, mUnit;
    LinearLayout progressBar, blured;
    TextView publish;
    private List<Uri> imageUris = new ArrayList<>();
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    DatabaseReference databaseReference;
    DatabaseReference categoriesReference;
    StorageReference storageReference;
    ArrayAdapter<Category> adapterItems;
    ArrayAdapter<String> adapterUnit;
    TextInputLayout loc;
    String[] items2 = {"Alliums", "Leafy Greens", "Nightshade", "Fruit Vegetables", "Legumes"};
    String[] items = {"Kilo", "Tali", "Sack", "Per Piece"};
    Toolbar toolbar;
    private LocationCallback locationCallback;
    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private ProgressBar locationProgressBar;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_add);

        blured = findViewById(R.id.blured);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Listing");
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Marketplace");
        categoriesReference = FirebaseDatabase.getInstance().getReference("Category");
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        mCategory = findViewById(R.id.MaddCategory);

        mCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items2));
        adapterUnit = new ArrayAdapter<>(this, R.layout.item_category, items);

        mUnit = findViewById(R.id.priceCateg);
        mUnit.setAdapter(adapterUnit);

        intApproved = findViewById(R.id.intApprove);
        viewPager = findViewById(R.id.viewPager);
        viewPagerOverlay = findViewById(R.id.viewPagerOverlay);
        imageAdapter = new ImageAdapter(imageUris);
        viewPager.setAdapter(imageAdapter);
        mTitle = findViewById(R.id.MaddTitle);
        mPrice = findViewById(R.id.MaddPrice);
        mQuantity = findViewById(R.id.MaddQuantity);
        mDescription = findViewById(R.id.MaddDescription);
        businessAdd = findViewById(R.id.businessAdd);
//        mStoreLocation = findViewById(R.id.MaddStoreLoc);
//        loc = findViewById(R.id.loc);
        mVendorDB = findViewById(R.id.mVendor);
        publish = findViewById(R.id.publish);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        //locationProgressBar = findViewById(R.id.locationProgressBar);

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                mVendorDB.setText(value.getString("Fullname"));
                businessAdd.setText(value.getString("BusinessAddress"));
            }
        });

        mUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(), "Items: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        selectedImages = new ArrayList<>();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                if (data.getClipData() != null) {
                                    int count = data.getClipData().getItemCount();
                                    for (int i = 0; i < count && selectedImages.size() < MAX_IMAGE_COUNT; i++) {
                                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                        selectedImages.add(imageUri);
                                    }
                                } else if (data.getData() != null) {
                                    selectedImages.add(data.getData());
                                }
                                displaySelectedImages();
                            }
                        } else {
                            Toast.makeText(Market_Add.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        viewPagerOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedImages.size() < MAX_IMAGE_COUNT) {
                    Intent photoPicker = new Intent(Intent.ACTION_PICK);
                    photoPicker.setType("image/*");
                    photoPicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    activityResultLauncher.launch(photoPicker);
                } else {
                    Toast.makeText(Market_Add.this, "Max 5 images allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

//        Button getLocationButton = findViewById(R.id.getLocationButton); // Assuming you have a button to get location
//        getLocationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getCurrentLocation();
//            }
//        });
//
//        requestLocationPermission();

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedImages.isEmpty()) {
                    uploadToFirebase(selectedImages);
                    blured.setBackgroundColor(ContextCompat.getColor(Market_Add.this, R.color.gray));
                } else {
                    Toast.makeText(Market_Add.this, "Please select images", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displaySelectedImages() {
        imageUris.clear();
        imageUris.addAll(selectedImages);
        imageAdapter.notifyDataSetChanged();
    }

    private void removeImage(int position) {
        if (position < selectedImages.size()) {
            selectedImages.remove(position);
            displaySelectedImages();
        }
    }

//    private void requestLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_LOCATION_PERMISSION);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_LOCATION_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getCurrentLocation();
//            } else {
//                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

//    private void getCurrentLocation() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            LocationRequest locationRequest = LocationRequest.create();
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            locationRequest.setInterval(10000);
//            locationRequest.setFastestInterval(5000);
//            locationProgressBar.setVisibility(View.VISIBLE);
//
//            locationCallback = new LocationCallback() {
//                @Override
//                public void onLocationResult(LocationResult locationResult) {
//                    if (locationResult == null) {
//                        return;
//                    }
//                    for (Location location : locationResult.getLocations()) {
//                        if (location != null) {
//                            double latitude = location.getLatitude();
//                            double longitude = location.getLongitude();
//                            getAddressFromLocation(latitude, longitude);
//                            fusedLocationProviderClient.removeLocationUpdates(locationCallback); // Stop updates after getting the location
//                            locationProgressBar.setVisibility(View.GONE);
//                        }
//                    }
//                }
//            };
//
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
//        } else {
//            requestLocationPermission();
//        }
//    }
//
//    private void getAddressFromLocation(double latitude, double longitude) {
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            if (addresses != null && !addresses.isEmpty()) {
//                Address address = addresses.get(0);
//                String addressText = address.getAddressLine(0); // Get full address
//                mStoreLocation.setText(addressText);
//            } else {
//                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to get address", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadToFirebase(List<Uri> uris) {
        progressBar.setVisibility(View.VISIBLE);
        blured.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        String title = mTitle.getText().toString();
        String price = mPrice.getText().toString();
        String quantity = mQuantity.getText().toString();
        String category = mCategory.getText().toString();
        String description = mDescription.getText().toString();
        String location = businessAdd.getText().toString();
        String vendor = mVendorDB.getText().toString();
        String intApprove = intApproved.getText().toString();
        String unit = mUnit.getText().toString();

        List<String> imageUrls = new ArrayList<>();
        for (Uri uri : uris) {
            final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrls.add(uri.toString());
                            if (imageUrls.size() == uris.size()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Map<String, Object> marketData = new HashMap<>();
                                marketData.put("category", category);
                                marketData.put("description", description);
                                marketData.put("price", price);
                                marketData.put("location", location);
                                marketData.put("mProduct", imageUrls);
                                marketData.put("quantity", quantity);
                                marketData.put("title", title);
                                marketData.put("unit", unit);
                                marketData.put("vendor", vendor);
                                String key = databaseReference.push().getKey();
                                databaseReference.child(key).setValue(marketData);
                                Toast.makeText(Market_Add.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(Market_Add.this, MainActivity.class);
                                intent.putExtra("marketFragment", true);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    blured.setBackgroundColor(ContextCompat.getColor(Market_Add.this, R.color.gray));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    blured.setBackgroundColor(Color.TRANSPARENT);
                    Toast.makeText(Market_Add.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private List<Uri> imageUris;

        public ImageAdapter(List<Uri> imageUris) {
            this.imageUris = imageUris;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imagelistings, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            Uri imageUri = imageUris.get(position);
            holder.imageView.setImageURI(imageUri);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedImages.size() < MAX_IMAGE_COUNT) {
                        Intent photoPicker = new Intent(Intent.ACTION_PICK);
                        photoPicker.setType("image/*");
                        photoPicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        activityResultLauncher.launch(photoPicker);
                    } else {
                        Toast.makeText(Market_Add.this, "Max 5 images allowed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeImage(position);
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageUris.size();
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }
}
