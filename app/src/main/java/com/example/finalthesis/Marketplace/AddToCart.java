package com.example.finalthesis.Marketplace;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddToCart extends AppCompatActivity implements CartAdapter.OnSelectionChangedListener{

    private RecyclerView recyclerView;
    LinearLayout checkOut;
    DatabaseReference databaseReference;
    CheckBox checkBox;
    TextView checked, priceTotal;
    CartAdapter cartAdapter;
    ArrayList<CartItem> cartItems;
    Toolbar toolbar;

    int selectedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add to Cart");
        checkOut = findViewById(R.id.checkOut);
        checked = findViewById(R.id.checked);
        priceTotal = findViewById(R.id.totalprice);
        checkBox = findViewById(R.id.checkbox_id);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, this, this);
        recyclerView.setAdapter(cartAdapter);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cartAdapter.setAllCheckBoxes(isChecked);
                updateCheckedCount();
            }
        });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CartItem> selectedItems = cartAdapter.getSelectedItems();

                if (selectedItems == null || selectedItems.isEmpty()) {
                    Toast.makeText(AddToCart.this, "Please select at least one item to proceed to checkout", Toast.LENGTH_SHORT).show();
                }
                else if (!allItemsFromSameVendor(selectedItems)) {
                    Toast.makeText(AddToCart.this, "Cannot proceed, one Vendor at a time only", Toast.LENGTH_SHORT).show();
                }
                else {
                    double totalPrice = cartAdapter.calculateTotalPriceOfSelectedItems();

                    Intent intent = new Intent(AddToCart.this, Checkout.class);
                    intent.putExtra("totalPrice", totalPrice);
                    intent.putExtra("totalOrderQuantity", selectedCount);
                    intent.putParcelableArrayListExtra("selectedItems", (ArrayList<? extends Parcelable>) selectedItems);
                    startActivity(intent);
                }
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                Map<String, Integer> titleCountMap = new HashMap<>(); // To keep track of title counts
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CartItem cartItem = dataSnapshot.getValue(CartItem.class);
                    if (cartItem != null) {
                        String title = cartItem.getTitle();
                        if (!titleCountMap.containsKey(title)) {
                            titleCountMap.put(title, 1);
                            cartItem.setKey(dataSnapshot.getKey());
                            cartItems.add(cartItem);
                        } else {
                            int count = titleCountMap.get(title);
                            count++;
                            titleCountMap.put(title, count);
                        }
                    }
                }
                for (CartItem item : cartItems) {
                    String title = item.getTitle();
                    int count = titleCountMap.get(title);
                    item.setCount(count);
                }

                cartAdapter.notifyDataSetChanged();
                updateCheckedCount();

                Log.d("FirebaseData", "Data retrieved: " + cartItems.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddToCart.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.back_button);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private boolean allItemsFromSameVendor(List<CartItem> selectedItems) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            return true;
        }
        String firstVendor = selectedItems.get(0).getVendor();
        for (CartItem item : selectedItems) {
            if (!item.getVendor().equals(firstVendor)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onPriceChanged(double totalPrice) {
        priceTotal.setText(String.format("%.2f", totalPrice));
    }

    @Override
    public void onSelectionChanged(int selectedCount) {
        this.selectedCount = selectedCount;
        checked.setText("" + selectedCount);
    }

    private void updateCheckedCount() {
        int selectedCount = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                selectedCount++;
            }
        }
        checked.setText("" + selectedCount);
        this.selectedCount = selectedCount;
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
