package com.example.finalthesis.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.Marketplace.AddToCart;
import com.example.finalthesis.Marketplace.ApplyToVendor;
import com.example.finalthesis.Marketplace.MarketAdapter;
import com.example.finalthesis.Marketplace.MarketData;
import com.example.finalthesis.Marketplace.Market_Add;
import com.example.finalthesis.Marketplace.ProfileVendorView;
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

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Marketplace extends Fragment {

    Spinner spinner;
    ArrayAdapter<String> adapterItems;
    String[] items = {"All", "Alliums", "Leafy Greens", "Nightshade", "Fruit Vegetables", "Legumes"};
    LinearLayout sell, categProduct, alert;
    Button yesVendor, noVendor;
    ImageButton profileView, cart;
    RecyclerView recyclerView;
    ArrayList<MarketData> marketList;
    MarketAdapter marketAdapter;
    SearchView searchView;
    DatabaseReference databaseReference, paymentsRef ;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    TextView vendorName, selectedCategoryText, numCart,numCart2;
    DatabaseReference cartRef;
    String isVendorValue, gcashNumber, gcashName, gcashQr;
    RelativeLayout blured;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_marketplace, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("Marketplace");
        paymentsRef = FirebaseDatabase.getInstance().getReference("Payment");
        recyclerView = view.findViewById(R.id.MrecycleView);
        profileView = view.findViewById(R.id.profileViewM);
        cart = view.findViewById(R.id.cart);
        sell = view.findViewById(R.id.sell_);
        categProduct = view.findViewById(R.id.categProduct);
        numCart = view.findViewById(R.id.numCart);
        numCart2 = view.findViewById(R.id.numCart2);
        alert = view.findViewById(R.id.alertVendor);
        yesVendor = view.findViewById(R.id.yesVendor);
        noVendor = view.findViewById(R.id.noVendor);
        blured = view.findViewById(R.id.blured);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);
        fetchCartCount();
        fetchCount2();

        selectedCategoryText = view.findViewById(R.id.selectedCategoryText);
        spinner = view.findViewById(R.id.spinner);
        adapterItems = new ArrayAdapter<>(getContext(), R.layout.item_category, items);
        adapterItems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterItems);

        int numberOfColumns = 2;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        marketList = new ArrayList<>();
        marketAdapter = new MarketAdapter(marketList, requireContext());
        recyclerView.setAdapter(marketAdapter);
        searchView = view.findViewById(R.id.search1);
        searchView.clearFocus();
        vendorName = view.findViewById(R.id.vNames);

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(executorService, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    vendorName.setText(value.getString("Fullname"));
                }
            }
        });

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Vendors").child(userId);
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String isVendor = dataSnapshot.child("vendor").getValue(String.class);
                    String gcashNameGet = dataSnapshot.child("GcashName").getValue(String.class);
                    String gcashNumberGet = dataSnapshot.child("GcashNumber").getValue(String.class);
                    String gcashQrGet = dataSnapshot.child("GcashQr").getValue(String.class);
                    isVendorValue = isVendor;
                    gcashName = gcashNameGet;
                    gcashNumber = gcashNumberGet;
                    gcashQr = gcashQrGet;

                    // Check visibility here
                    if (Objects.equals(gcashNumber, "") || Objects.equals(gcashName, "") || Objects.equals(gcashQr, "")) {
                        profileView.setVisibility(View.GONE);
                    } else {
                        profileView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        yesVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), ApplyToVendor.class);
                startActivity(intent);
                blured.setVisibility(View.GONE);
                alert.setVisibility(View.GONE);
            }
        });

        noVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.setVisibility(View.GONE);
                blured.setVisibility(View.GONE);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = items[position];
                selectedCategoryText.setText(selectedCategory);
                fetchData(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(gcashNumber, "") || Objects.equals(gcashName, "") || Objects.equals(gcashQr, "")) {
                    Intent intent = new Intent(requireContext(), ProfileVendorView.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(requireContext(), Market_Add.class);
                    startActivity(intent);
                }
            }
        });

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), ProfileVendorView.class);
                startActivity(intent);
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

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), AddToCart.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCartCount();
        fetchCount2();
    }

    private void fetchCount2() {
        paymentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countItems = 0; // Reset count

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String vendor = snapshot.child("vendor").getValue(String.class);
                    String confirm = snapshot.child("isConfirm").getValue(String.class);
                    if (vendor != null && vendor.equals(vendorName.getText().toString()) && confirm == null) {
                        countItems++;
                    }
                }

                if (countItems > 0) {
                    numCart2.setText(String.valueOf(countItems));
                    numCart2.setVisibility(View.VISIBLE);
                } else {
                    numCart2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.e("PaymentCount", "Error counting payments: " + databaseError.getMessage());
            }
        });
    }

    public void searchList(String text) {
        ArrayList<MarketData> searchList = new ArrayList<>();
        String selectedCategory = spinner.getSelectedItem().toString(); // Get the selected category from the spinner

        for (MarketData marketData : marketList) {
            // Check if the item matches the selected category and contains the search text in the description
            if ((selectedCategory.equals("All") || marketData.getCategory().equals(selectedCategory))
                    && marketData.getDescription().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(marketData);
            }
        }
        marketAdapter.searchDataList(searchList);
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
            }
        });
    }



    private void fetchData(String selectedCategory) {
        marketList.clear();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String vendor = vendorName.getText().toString();
                String zero = "0";
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        MarketData marketData = dataSnapshot.getValue(MarketData.class);
                        if (!vendor.equals(marketData.getVendor()) && !zero.equals(marketData.getQuantity())) {//marketData != null && "2".equals(marketData.getCategory()) &&
                            if ("All".equals(selectedCategory) || marketData.getCategory().equals(selectedCategory)) {
                                marketData.setKey(dataSnapshot.getKey());
                                marketList.add(marketData);
                            }
                        }
                    } catch (DatabaseException e) {
                        Log.e("FirebaseError", "Error converting data to DataClass", e);
                    }
                }
                marketAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database error: " + error.getMessage());
            }
        });
    }
}