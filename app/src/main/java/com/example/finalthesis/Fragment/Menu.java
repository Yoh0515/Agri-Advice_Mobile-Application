package com.example.finalthesis.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.finalthesis.Login;
import com.example.finalthesis.Menu.Order;
import com.example.finalthesis.Menu.Reports_Module;
import com.example.finalthesis.Menu.Sales;
import com.example.finalthesis.Menu.Seminar_Report_Module;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Menu extends Fragment {

    TextView profileN;
    ImageView profileV;
    LinearLayout ProfileView, ProductApproval, Logout, Message, order, PlantDisease,NewsUpdate,PriceList,Settings,AboutUs, CustomerComplaint, SeminarRequest;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    RelativeLayout viewImage;
    ImageView Vimage2;
    private Context context;
    String isVendorValue = "";

    String getCurrentUserName, getCurrentAddress, getCurrentPhoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        context = getContext();
        viewImage = view.findViewById(R.id.viewImage);
        Vimage2 = view.findViewById(R.id.Vimage2);
        ProfileView = view.findViewById(R.id.profileView);
        profileN = view.findViewById(R.id.profileN);
        profileV = view.findViewById(R.id.profileV);
        Message = view.findViewById(R.id.messagelist);
        CustomerComplaint = view.findViewById(R.id.customerComplaint);
        SeminarRequest = view.findViewById(R.id.seminarRequest);
        order = view.findViewById(R.id.order);
        NewsUpdate = view.findViewById(R.id.nu);
        PriceList = view.findViewById(R.id.pl);
//        PlantDisease = view.findViewById(R.id.pd);
//        Settings = view.findViewById(R.id.settings);
//        AboutUs = view.findViewById(R.id.aboutUs);
        Logout = view.findViewById(R.id.logout);

        ProductApproval = view.findViewById(R.id.productApproval);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(executorService, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    String fullname = value.getString("Fullname");
                    if (fullname != null) {
                        // Update UI on the main thread
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                profileN.setText(fullname);
                            }
                        });
                    }
                }
            }
        });


        profileV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewImage.setVisibility(View.VISIBLE);
            }
        });

        viewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewImage.setVisibility(View.GONE);
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
                            Glide.with(context).load(profileImageUrl).into(profileV);
                            Glide.with(context).load(profileImageUrl).into(Vimage2);
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

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Vendors").child(userId);
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String isVendor = dataSnapshot.child("vendor").getValue(String.class);
                    isVendorValue = isVendor;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        CollectionReference _usersCollectionInfoReference = FirebaseFirestore.getInstance().collection("Users");
        String currentUserUid = fAuth.getCurrentUser().getUid();
        DocumentReference getUserName = _usersCollectionInfoReference.document(currentUserUid);

        getUserName.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String email = documentSnapshot.getString("UserEmail");
                    String fullName = documentSnapshot.getString("Fullname");
                    String Address = documentSnapshot.getString("Address");
                    String PhoneNumber = documentSnapshot.getString("Number");
                    if (email != null) {
                        getCurrentUserName = fullName;
                        getCurrentAddress = Address;
                        getCurrentPhoneNumber = PhoneNumber;
                    } else {
                        Toast.makeText(context.getApplicationContext(), "Vendor email not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context.getApplicationContext(), "Failed to get vendor email", Toast.LENGTH_SHORT).show();
            }
        });






        ProductApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if ("true".equals(isVendorValue)) {
                    Intent intent = new Intent(requireContext(), com.example.finalthesis.Marketplace.ProductApproval.class);
                    startActivity(intent);
//                } else {
//                    Toast.makeText(requireContext(), "This feature is available for Vendor.", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(requireContext(), com.example.finalthesis.Menu.MessageList.class);
                intent.putExtra("CURRENT_USER_NAME", getCurrentUserName);
                startActivity(intent);
            }
        });


        CustomerComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUserEmail = fAuth.getCurrentUser().getEmail();
                String adminEmail = "agriadvice123@gmail.com";
                String threadId = generateThreadId(currentUserEmail, adminEmail);

                Intent intent = new Intent(getActivity(), com.example.finalthesis.Marketplace.MessageAdmin.class);
                intent.putExtra("THREAD_ID", threadId);
                intent.putExtra("CURRENT_USER_EMAIL", currentUserEmail);
                intent.putExtra("ADMIN_NAME", "Agri Advice");
                intent.putExtra("CURRENT_USER_NAME", getCurrentUserName);
                intent.putExtra("ADMIN_EMAIL", adminEmail);
                startActivity(intent);
            }
        });

        CustomerComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Reports_Module.class);
                startActivity(intent);
            }
        });

        SeminarRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Seminar_Report_Module.class);
                startActivity(intent);
            }
        });

//        SeminarRequest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String currentUserEmail = fAuth.getCurrentUser().getEmail();
//                String adminEmail = "agriadvice123@gmail.com";
//                String threadId = generateThreadId(currentUserEmail, adminEmail);
//
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                DocumentReference docRef = db.collection("Seminar").document(threadId);
//
//                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            Intent intent;
//                            if (document.exists()) {
//                                // Thread ID exists, go to MessagesAdminSeminar
//                                intent = new Intent(getActivity(), com.example.finalthesis.Marketplace.MessagesAdminSeminar.class);
//                            } else {
//                                // Thread ID does not exist, go to SeminarForms
//                                intent = new Intent(getActivity(), com.example.finalthesis.Marketplace.SeminarForms.class);
//                            }
//                            intent.putExtra("THREAD_ID", threadId);
//                            intent.putExtra("CURRENT_USER_EMAIL", currentUserEmail);
//                            intent.putExtra("ADMIN_NAME", "Agri Advice");
//                            intent.putExtra("CURRENT_USER_NAME", getCurrentUserName);
//                            intent.putExtra("CURRENT_USER_ADDRESS", getCurrentAddress);
//                            intent.putExtra("CURRENT_USER_NUMBER", getCurrentPhoneNumber);
//                            intent.putExtra("ADMIN_EMAIL", adminEmail);
//                            startActivity(intent);
//                        } else {
//                            Log.d("Firestore", "Error getting document: ", task.getException());
//                        }
//                    }
//                });
//            }
//        });


//        PlantDisease.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(requireContext(), com.example.finalthesis.PestandDisease.Activity_PestandDisease.class);
//                startActivity(intent);
//            }
//        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), Order.class);
                startActivity(intent);
            }
        });



        NewsUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), com.example.finalthesis.Menu.NewsUpdate.class);
                startActivity(intent);
            }
        });

        PriceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), Sales.class);
                startActivity(intent);
            }
        });

//        Settings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(requireContext(), com.example.finalthesis.Menu.Settings.class);
//                startActivity(intent);
//            }
//        });

//        AboutUs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(requireContext(), com.example.finalthesis.Menu.AboutUs.class);
//                startActivity(intent);
//            }
//        });

        ProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), com.example.finalthesis.Menu.EditProfile.class);
                startActivity(intent);
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                Intent intent = new Intent(requireContext(), Login.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private String generateThreadId(String email1, String email2) {
        if (email1.compareTo(email2) < 0) {
            return email1 + "_" + email2;
        } else {
            return email2 + "_" + email1;
        }
    }
}

