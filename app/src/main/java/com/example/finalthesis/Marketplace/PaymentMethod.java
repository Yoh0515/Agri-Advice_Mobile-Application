package com.example.finalthesis.Marketplace;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentMethod extends AppCompatActivity {

    private static final String TAG = "MyTag";
    TextView idNum, dateNum, totalPrice, totalItems, totalOrders, name, status, invoice, gName, gNum;
    ImageView qrCode;
    Button buttonPayment, buttonConfirm;
    LinearLayout itemDetailsLayout, alertPayment;
    RelativeLayout paymentBlur;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Toolbar toolbar;
    String vendor = "";
    ArrayList<CartItem> cartItems;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        paymentBlur = findViewById(R.id.paymentBlur);
        alertPayment = findViewById(R.id.alertPayment);
        qrCode = findViewById(R.id.qrCode);
        toolbar = findViewById(R.id.toolbar);
        gName = findViewById(R.id.nameG);
        gNum = findViewById(R.id.numG);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payment");
        itemDetailsLayout = findViewById(R.id.itemDetailsLayout);
        idNum = findViewById(R.id.text_id_number);
        dateNum = findViewById(R.id.text_current_date);
        totalPrice = findViewById(R.id.text_total_price);
        totalItems = findViewById(R.id.text_total_items);
        totalOrders = findViewById(R.id.text_total_orders);
        name = findViewById(R.id.text_name);
        status = findViewById(R.id.text_status);
        buttonConfirm = findViewById(R.id.contI);
        buttonPayment = findViewById(R.id.paymentB);
        invoice = findViewById(R.id.invoiceB);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("Fullname"));
            }
        });

        buttonPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertPayment.setVisibility(View.VISIBLE);
                paymentBlur.setVisibility(View.VISIBLE);
                paymentBlur.setBackgroundColor(ContextCompat.getColor(PaymentMethod.this, R.color.gray));
            }
        });

        alertPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertPayment.setVisibility(View.GONE);
                paymentBlur.setVisibility(View.GONE);
                paymentBlur.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        paymentBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertPayment.setVisibility(View.GONE);
                paymentBlur.setVisibility(View.GONE);
                paymentBlur.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        dateNum.setText(formattedDateTime);

        String transactionId = shortenUUID(UUID.randomUUID().toString());
        String invoiceNumber = shortenUUID(UUID.randomUUID().toString());

        Intent intent = getIntent();
        double tPrice = intent.getDoubleExtra("totalPrice", 0.0);
        List<CartItem> selectedItems = intent.getParcelableArrayListExtra("selectedItems");

        idNum.setText(transactionId);
        invoice.setText("Invoice No.: " + invoiceNumber);
        totalPrice.setText("₱" + String.format("%.2f", tPrice));

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
                totalItems.setText("Total Items Quantity: " + totalQuantity);
                vendor = item.getVendor();


            }

            totalOrders.setText("Total Orders: " + String.valueOf(selectedItems.size()));

        } else {
            totalItems.setText("Total Items: 0");
            totalOrders.setText("Total Orders: 0");
        }

        DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference().child("Vendors");
        vendorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String accName = snapshot.child("GcashName").getValue(String.class);
                    String vendorname = snapshot.child("vendorName").getValue(String.class);
                    String accNum = snapshot.child("GcashNumber").getValue(String.class);
                    String payImage = snapshot.child("GcashQR").getValue(String.class);

                    if (vendorname.equals(vendor)) {
                        gName.setText(accName);
                        gNum.setText(accNum);
                        Glide.with(PaymentMethod.this)
                                .load(payImage)
                                .into(qrCode);
                    } else {
//                        Toast.makeText(PaymentMethod.this, "Loading QrCod", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
                Log.e("PaymentMethod", "Database error: " + databaseError.getMessage());
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentMethod.this, PaymentConfirmation.class);
                intent.putExtra("transactionId", idNum.getText().toString());
                intent.putExtra("totalPrice", totalPrice.getText().toString());
                intent.putExtra("currentDate", dateNum.getText().toString());
                intent.putExtra("vendor", vendor);
                intent.putParcelableArrayListExtra("selectedItems", (ArrayList<? extends Parcelable>) selectedItems);
                startActivity(intent);
            }
        });
    }

    private String shortenUUID(String uuid) {
        return uuid.replaceAll("-", "").substring(0, 20);
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
