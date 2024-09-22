package com.example.finalthesis.Marketplace;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class Checkout extends AppCompatActivity {

    Toolbar toolbar;
    TextView allPrice, totalItem, buyerName, buyerNumber, buyerAdd, totalOrderNumber;
    Button placeOrder;
    LinearLayout itemDetailsLayout;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        placeOrder = findViewById(R.id.buttonOrder);
        buyerName = findViewById(R.id.buyerName);
        buyerAdd = findViewById(R.id.buyerAddress);
        buyerNumber = findViewById(R.id.buyerNumber);
        allPrice = findViewById(R.id.totalPriceTextView);
        totalItem = findViewById(R.id.totalItem);
        itemDetailsLayout = findViewById(R.id.itemDetailsLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Checkout");
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        totalOrderNumber = findViewById(R.id.totalOrderQuantity);

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    buyerName.setText(value.getString("Fullname"));
                    buyerAdd.setText(value.getString("Address"));
                    buyerNumber.setText(value.getString("Number"));
                }
            }
        });

        Intent intent = getIntent();
        double totalPrice = intent.getDoubleExtra("totalPrice", 0.0);
        List<CartItem> selectedItems = intent.getParcelableArrayListExtra("selectedItems");
        int totalOrderQuantity = intent.getIntExtra("totalOrderQuantity", 0);

        if (selectedItems != null) {
            int totalQuantity = 0;
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

                totalQuantity += item.getCartCount();
            }
            totalItem.setText(String.valueOf(selectedItems.size()));
            totalOrderNumber.setText("Total Quantity: " + totalQuantity);

        } else {
            totalItem.setText("0");
            totalOrderNumber.setText("Total Quantity: 0");
        }

        allPrice.setText("₱" + String.format("%.2f", totalPrice));

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Checkout.this, PaymentMethod.class);
                intent.putExtra("totalPrice", totalPrice);
                intent.putParcelableArrayListExtra("selectedItems", (ArrayList<? extends Parcelable>) selectedItems);
                startActivity(intent);
            }
        });

        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.back_button);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
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
