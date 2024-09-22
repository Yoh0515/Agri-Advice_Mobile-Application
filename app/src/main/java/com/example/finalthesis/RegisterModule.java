package com.example.finalthesis;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegisterModule extends AppCompatActivity {

    private String firstName, lastName, middleName, number;
    private String blk, lot, subdivision, city, postalCode, barangay;
    private String username, email, password, confirmPassword;

    private String locationText;
    private boolean valid = true;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_module);

//        if(middleName.equals("N/A") || middleName.equals("n/a")){
//            middleName = "";
//        }

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (savedInstanceState == null) {
            loadFragment(new UserDetailsFragment(), false);
        }
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    public void setUserDetails(String firstName, String lastName, String middleName, String number) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.number = number;

    }

    public void setAddressDetails(String blk, String lot, String subdivision, String city, String postalCode, String barangay) {
        this.blk = blk;
        this.lot = lot;
        this.subdivision = subdivision;
        this.barangay = barangay;
        this.city = city;
        this.postalCode = postalCode;
    }

    public void setAccountDetails(String username, String email, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public void getCurrentLocationAndRegister() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        Toast.makeText(RegisterModule.this, "Failed to fetch location", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            getAddressFromLocation(latitude, longitude);
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback); // Stop updates after getting the location

                            // Proceed with registration after fetching location
                            if (locationText != null) {
                                registerUser();
                            } else {
                                Toast.makeText(RegisterModule.this, "Failed to get address", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                    }
                }
            };

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        } else {
            requestLocationPermission();
        }
    }

    private void registerUser() {
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = fAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterModule.this, "Verification Email Sent. Please check your email.", Toast.LENGTH_SHORT).show();
                                                fAuth.signOut();
                                                storeUserData(user);
                                                showVerificationDialog();
                                            } else {
                                                Toast.makeText(RegisterModule.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterModule.this, "Failed to Create an Account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeUserData(FirebaseUser user) {
        // Store user data in Realtime Database
        DatabaseReference databaseReference, vendorsReference;
        String userId = user.getUid();

        DocumentReference df = fStore.collection("Users").document(userId);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        vendorsReference = FirebaseDatabase.getInstance().getReference("Vendors").child(userId);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("FirstName", firstName);
        userInfo.put("LastName", lastName);
        userInfo.put("MiddleName", middleName);
        userInfo.put("Fullname", firstName + " " + middleName + " " + lastName);
        userInfo.put("Blk", blk);
        userInfo.put("Lot", lot);
        userInfo.put("Subdivision", subdivision);
        userInfo.put("Number", number);
        userInfo.put("City", city);
        userInfo.put("PostalCode", postalCode);
        userInfo.put("Username", username);
        userInfo.put("UserEmail", email);
        userInfo.put("Address", "Blk" +" "+blk + ", " + "Lot" +" "+ lot + ", " + subdivision + ", " + barangay + ", " + city + ", " + postalCode);
        userInfo.put("Location", locationText);

        Map<String, Object> vendorInfo = new HashMap<>();
        vendorInfo.put("vendorName", firstName + " " + middleName + " " + lastName);
        vendorInfo.put("vendorEmail", email);
        vendorInfo.put("vendorUsername", username);
        vendorInfo.put("Address", "Blk" +" "+blk + ", " + "Lot" +" "+ lot + ", " + subdivision + ", " + barangay + ", " + city + ", " + postalCode);
        vendorInfo.put("Location", locationText);
        vendorInfo.put("Number", number);
        vendorInfo.put("vendor", "true");
        vendorInfo.put("GcashName", "");
        vendorInfo.put("GcashNumber", "");
        vendorInfo.put("GcashQR", "");
        vendorInfo.put("BusinessAddress", "");
        vendorInfo.put("idImageUrl", "");
        vendorInfo.put("idNumber", "");
        vendorInfo.put("profileImage", "");

        df.set(userInfo);
        databaseReference.setValue(userInfo);
        vendorsReference.setValue(vendorInfo);

    }

    private void showVerificationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Email Verification")
                .setMessage("Please verify your email and then log in.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(RegisterModule.this, Login.class));
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location access
                getCurrentLocation();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            getAddressFromLocation(latitude, longitude);
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback); // Stop updates after getting the location

                            // Proceed with registration after fetching location
                            if (locationText != null) {
                                registerUser();
                            } else {
                                Toast.makeText(RegisterModule.this, "Failed to get address", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                    }
                }
            };

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        }
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                locationText = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
