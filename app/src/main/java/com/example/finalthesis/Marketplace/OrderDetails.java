package com.example.finalthesis.Marketplace;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderDetails extends AppCompatActivity {

    Toolbar toolbar;
    TextView date, name, transID, price, reference, vname, vend, cosAddress, orderQuantity;
    ImageView imagePay;
    LinearLayout confirm, notConfirm, itemLay, yesConfirm, buttons;
    String key = "";
    String imageUrl = "";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        vend = findViewById(R.id.vendor);
        itemLay = findViewById(R.id.item);
        vname = findViewById(R.id.nameV);
        date = findViewById(R.id.conDate);
        name = findViewById(R.id.conName);
        transID = findViewById(R.id.conTrans);
        price = findViewById(R.id.conPrice);
        reference = findViewById(R.id.conReference);
        imagePay = findViewById(R.id.conImage);
        confirm = findViewById(R.id.Confirm);
        notConfirm = findViewById(R.id.notConfirm);
        yesConfirm = findViewById(R.id.yesConfirm);
        buttons = findViewById(R.id.buttons);
        cosAddress = findViewById(R.id.cosAddress);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener((value, error) -> {
            if (value != null) {
                vname.setText(value.getString("Fullname"));
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            date.setText(bundle.getString("Date"));
            reference.setText(bundle.getString("Reference"));
            price.setText(bundle.getString("Price"));
            transID.setText(bundle.getString("Trans"));
            name.setText(bundle.getString("Name"));
            key = bundle.getString("Key");
            vend.setText(bundle.getString("Vendor"));
            Glide.with(this).load(bundle.getString("Image")).into(imagePay);
            imageUrl = bundle.getString("Image");
        }

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        fetchUserDetails(executorService);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vname.getText().toString().equals(vend.getText().toString())) {
                    DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference().child("Payment").child(key);
                    paymentRef.child("isConfirm").setValue("true");

                    DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Order");
                    ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                                    String vendor = orderSnapshot.child("vendor").getValue(String.class);
                                    String key = orderSnapshot.getKey();
                                    if (vendor != null && vend.getText().toString().equals(vendor)) {
                                        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Order").child(key);
                                        orderRef.child("isConfirm").setValue("true");
                                    }
                                }
                            } else {
                                Toast.makeText(OrderDetails.this, "No orders found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(OrderDetails.this, "Error getting data from 'Order' node: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                yesConfirm.setVisibility(View.VISIBLE);
                buttons.setVisibility(View.GONE);
            }
        });

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Order");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        String dateO = orderSnapshot.child("date").getValue(String.class);
                        String image0 = orderSnapshot.child("image").getValue(String.class);
                        String title0 = orderSnapshot.child("title").getValue(String.class);
                        String unit0 = orderSnapshot.child("unit").getValue(String.class);
                        String vendor0 = orderSnapshot.child("vendor").getValue(String.class);
                        String price0 = orderSnapshot.child("price").getValue(String.class);
                        int itemQuantity0 = orderSnapshot.child("itemQuantity").getValue(int.class);
                        String delivery0 = orderSnapshot.child("deliveryFee").getValue(String.class);

                        if (dateO != null && dateO.equals(date.getText().toString())) {

                            LinearLayout itemLayout = new LinearLayout(OrderDetails.this);
                            LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                            itemLayoutParams.setMargins(20, 0, 0, 20);
                            itemLayout.setLayoutParams(itemLayoutParams);
                            itemLayout.setOrientation(LinearLayout.HORIZONTAL);

                            TextView itemTextView = new TextView(OrderDetails.this);
                            itemTextView.setText("" + title0 + "\n"
                                    + "₱" + price0 +
                                    " / " + unit0 + "\n"
                                    + "Qty:" +
                                    itemQuantity0 + "\n"
                                    + "Delivery Fee: " + delivery0);

                            itemTextView.setTextSize(20);

                            CardView cardView = new CardView(OrderDetails.this);
                            LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                                    400,
                                    400
                            );
                            cardLayoutParams.setMargins(0, 0, 20, 0);
                            cardView.setLayoutParams(cardLayoutParams);
                            cardView.setRadius(5);

                            ImageView itemImageView = new ImageView(OrderDetails.this);
                            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT
                            );
                            itemImageView.setLayoutParams(imageLayoutParams);
                            itemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Glide.with(OrderDetails.this)
                                    .load(image0)
                                    .into(itemImageView);

                            cardView.addView(itemImageView);

                            itemLayout.addView(cardView);
                            itemLayout.addView(itemTextView);

                            itemLay.addView(itemLayout);
                        }
                    }
                } else {
                    Toast.makeText(OrderDetails.this, "No data found in 'Order' node", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderDetails.this, "Error getting data from 'Order' node: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserDetails(ExecutorService executorService) {
        String senderName = name.getText().toString();
        fStore.collection("Users")
                .whereEqualTo("Fullname", senderName)
                .get()
                .addOnCompleteListener(executorService, task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String address = document.getString("Address");
                            runOnUiThread(() -> cosAddress.setText(address));
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(OrderDetails.this, "Error getting user details", Toast.LENGTH_SHORT).show());
                    }
                });
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