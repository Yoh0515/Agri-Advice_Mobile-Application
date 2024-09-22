package com.example.finalthesis.Menu;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderHistory2 extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout feedbackLayout, lenOut,itemLay,itemLay2;
    TextView date, totalPrice, number, address ,transId,profileN;
    Button orderR;
    ProgressBar progressbar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history2);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Completed");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        profileN =findViewById(R.id.profileN);

        itemLay = findViewById(R.id.item);
        date = findViewById(R.id.date);
        totalPrice = findViewById(R.id.totalPrice);
        transId = findViewById(R.id.transId);
        lenOut = findViewById(R.id.lenOut);
        number = findViewById(R.id.number);
        address = findViewById(R.id.address);

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(executorService, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                profileN.setText(value.getString("Fullname"));
                address.setText(value.getString("Address"));
                number.setText(value.getString("Number"));
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            totalPrice.setText(bundle.getString("TotalP"));
            date.setText(bundle.getString("Date"));
            transId.setText(bundle.getString("TransID"));
        }





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
                        int qty0 = orderSnapshot.child("itemQuantity").getValue(int.class);
                        String delivery0 = orderSnapshot.child("deliveryFee").getValue(String.class);
                        String price0 = orderSnapshot.child("price").getValue(String.class);

                        if (dateO != null && dateO.equals(date.getText().toString())) {

                            LinearLayout itemLayout = new LinearLayout(OrderHistory2.this);
                            LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                            itemLayoutParams.setMargins(10, 0, 0, 10);
                            itemLayout.setLayoutParams(itemLayoutParams);
                            itemLayout.setOrientation(LinearLayout.HORIZONTAL);

                            TextView itemTextView = new TextView(OrderHistory2.this);
                            itemTextView.setText("" + title0 + "\n"
                                    + "₱" + price0 +
                                    " / " + unit0 + "\n"
                                    + "Qty: " + qty0 + "\n"
                                    + "Delivery Fee: " + delivery0);
                            itemTextView.setTextSize(20);

                            CardView cardView = new CardView(OrderHistory2.this);
                            LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                                    400,
                                    400
                            );
                            cardLayoutParams.setMargins(0, 0, 6, 0);
                            cardView.setLayoutParams(cardLayoutParams);
                            cardView.setRadius(5);

                            ImageView itemImageView = new ImageView(OrderHistory2.this);
                            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT
                            );
                            itemImageView.setLayoutParams(imageLayoutParams);
                            itemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Glide.with(OrderHistory2.this)
                                    .load(image0)
                                    .into(itemImageView);

                            cardView.addView(itemImageView);

                            itemLayout.addView(cardView);
                            itemLayout.addView(itemTextView);

                            itemLay.addView(itemLayout);
                        }
                    }
                } else {
                    Toast.makeText(OrderHistory2.this, "No data found in 'Order' node", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderHistory2.this, "Error getting data from 'Order' node: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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