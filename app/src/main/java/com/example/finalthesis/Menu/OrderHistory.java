package com.example.finalthesis.Menu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.MainActivity;
import com.example.finalthesis.Marketplace.MarketData;
import com.example.finalthesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class OrderHistory extends AppCompatActivity {

    private static final int REQUEST_IMAGE_GALLERY = 101;
    private Toolbar toolbar;
    private LinearLayout feedbackLayout, lenOut, itemLay, itemLay2;
    private TextView date, totalPrice, done, transId, profileN, number, address;
    private Button orderR;
    private ProgressBar progressbar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;
    private List<String> selectedImageUris = new ArrayList<>();
    private int currentFeedbackIndex = -1;
    private ArrayList<MarketData.Feedback> feedbackArray = new ArrayList<>();
    private List<List<String>> feedbackImagesList = new ArrayList<>();
    private StorageReference storageReference;

    private static final int TAG_IMAGE_LAYOUT_ID = 1234567890; // Custom unique key for imageLayoutId
    private static final int TAG_FEEDBACK_INDEX = 1234567891; // Custom unique key for feedbackIndex


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Deliver");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        profileN = findViewById(R.id.profileN);

        itemLay = findViewById(R.id.item);
        itemLay2 = findViewById(R.id.item2);
        feedbackLayout = findViewById(R.id.feedbackLayout);
        date = findViewById(R.id.date);
        totalPrice = findViewById(R.id.totalPrice);
        done = findViewById(R.id.done);
        transId = findViewById(R.id.transId);
        orderR = findViewById(R.id.orderR);
        lenOut = findViewById(R.id.lenOut);
        progressbar = findViewById(R.id.progressbar);
        number = findViewById(R.id.number);
        address = findViewById(R.id.address);



        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener((value, error) -> {
            if (value != null) {
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

        done.setOnClickListener(view -> {
            if (!isViewAttached()) {
                return;
            }

            Intent intent = new Intent(OrderHistory.this, MainActivity.class);
            intent.putExtra("menuFragment", true);
            startActivity(intent);
        });

        orderR.setOnClickListener(view -> {
            feedbackLayout.setVisibility(View.VISIBLE);
            lenOut.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
            orderR.setVisibility(View.GONE);

            DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference().child("Order");
            vendorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            String dateOrder = orderSnapshot.child("date").getValue(String.class);
                            if (dateOrder != null && dateOrder.equals(date.getText().toString())) {
                                String key = orderSnapshot.getKey();
                                DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference().child("Order").child(key);
                                paymentRef.child("recieved").setValue("true");
                                Toast.makeText(OrderHistory.this, "Order Received", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(OrderHistory.this, "Error getting data from 'Order' node: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            DatabaseReference payRef = FirebaseDatabase.getInstance().getReference().child("Payment");
            payRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            String dateOrder = orderSnapshot.child("paymentDate").getValue(String.class);
                            if (dateOrder != null && dateOrder.equals(date.getText().toString())) {
                                String key = orderSnapshot.getKey();
                                DatabaseReference paymetRef = FirebaseDatabase.getInstance().getReference().child("Payment").child(key);
                                paymetRef.child("recieved").setValue("true");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(OrderHistory.this, "Error getting data from 'Order' node: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
                        int qty0 = orderSnapshot.child("itemQuantity").getValue(int.class);
                        String delivery0 = orderSnapshot.child("deliveryFee").getValue(String.class);
                        String price0 = orderSnapshot.child("price").getValue(String.class);

                        if (dateO != null && dateO.equals(date.getText().toString())) {
                            LinearLayout itemLayout = createItemLayout(image0, title0, unit0, qty0,delivery0, price0);
                            itemLay.addView(itemLayout);
                        }
                    }
                } else {
                    Toast.makeText(OrderHistory.this, "No data found in 'Order' node", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderHistory.this, "Error getting data from 'Order' node: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference ordersRef2 = FirebaseDatabase.getInstance().getReference().child("Order");
        ordersRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    itemLay2.removeAllViews();
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        String dateO = orderSnapshot.child("date").getValue(String.class);
                        String image0 = orderSnapshot.child("image").getValue(String.class);
                        String title0 = orderSnapshot.child("title").getValue(String.class);
                        String unit0 = orderSnapshot.child("unit").getValue(String.class);
                        String vendor0 = orderSnapshot.child("vendor").getValue(String.class);
                        String price0 = orderSnapshot.child("price").getValue(String.class);

                        if (dateO != null && dateO.equals(date.getText().toString())) {
                            LinearLayout itemLayout = createItemLayoutWithFeedback(image0, title0, unit0, vendor0, price0);
                            itemLay2.addView(itemLayout);
                        }
                    }
                } else {
                    Toast.makeText(OrderHistory.this, "No data found in 'Order' node", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderHistory.this, "Error getting data from 'Order' node: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private LinearLayout createItemLayout(String image0, String title0, String unit0, int qty0, String price0, String delivery0) {
        LinearLayout itemLayout = new LinearLayout(OrderHistory.this);
        LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        itemLayoutParams.setMargins(20, 0, 0, 20);
        itemLayout.setLayoutParams(itemLayoutParams);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView itemTextView = new TextView(OrderHistory.this);
        itemTextView.setText("" + title0 + "\n"
                + "₱" + price0 +
                " / " + unit0 + "\n"
                + "Qty: " + qty0 + "\n"
                + "Delivery Fee: " + delivery0);
        itemTextView.setTextSize(20);

        CardView cardView = new CardView(OrderHistory.this);
        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                400,
                400
        );
        cardLayoutParams.setMargins(0, 0, 6, 0);
        cardView.setLayoutParams(cardLayoutParams);
        cardView.setRadius(5);

        ImageView itemImageView = new ImageView(OrderHistory.this);
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        itemImageView.setLayoutParams(imageLayoutParams);
        itemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(OrderHistory.this)
                .load(image0)
                .into(itemImageView);

        cardView.addView(itemImageView);

        itemLayout.addView(cardView);
        itemLayout.addView(itemTextView);

        return itemLayout;
    }

    private LinearLayout createItemLayoutWithFeedback(String image0, String title0, String unit0, String vendor0, String price0) {
        LinearLayout itemLayout = new LinearLayout(OrderHistory.this);
        LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        itemLayoutParams.setMargins(20, 0, 0, 20);
        itemLayout.setLayoutParams(itemLayoutParams);
        itemLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout headerLayout = new LinearLayout(OrderHistory.this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        CardView cardView = new CardView(OrderHistory.this);
        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                150,
                150
        );
        cardLayoutParams.setMargins(0, 0, 20, 0);
        cardView.setLayoutParams(cardLayoutParams);
        cardView.setRadius(5);

        ImageView itemImageView = new ImageView(OrderHistory.this);
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        itemImageView.setLayoutParams(imageLayoutParams);
        itemImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(OrderHistory.this)
                .load(image0)
                .into(itemImageView);

        cardView.addView(itemImageView);

        TextView itemTextView = new TextView(OrderHistory.this);
        itemTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        itemTextView.setText("Select 3 Images.");
        itemTextView.setText("" + title0 + "\n"
                + "₱" + price0 +
                " / " + unit0 + "\n"
                + "Vendor: " + "\n" + "     " + vendor0 + "\n");

        headerLayout.addView(cardView);
        headerLayout.addView(itemTextView);

        // Add feedback UI components
        EditText feedbackEditText = new EditText(OrderHistory.this);
        feedbackEditText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        feedbackEditText.setHint("Enter feedback");

        RatingBar ratingBar = new RatingBar(OrderHistory.this);
        LinearLayout.LayoutParams ratingBarParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ratingBar.setLayoutParams(ratingBarParams);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1.0f);

        LinearLayout imageLayout = new LinearLayout(OrderHistory.this);
        int imageLayoutId = View.generateViewId(); // Generate unique ID
        imageLayout.setId(imageLayoutId);
        imageLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        imageLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button addImageButton = new Button(OrderHistory.this);
        addImageButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        addImageButton.setText("Add Image");
        addImageButton.setOnClickListener(v -> {
            currentFeedbackIndex = itemLay2.indexOfChild(itemLayout);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
        });

        TextView textView = new TextView(OrderHistory.this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        textView.setText("  Select 3 Images\n");

        Button sendButton = new Button(OrderHistory.this);
        sendButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        sendButton.setText("Send Feedback");
        sendButton.setOnClickListener(v -> {

            String feedbackText = feedbackEditText.getText().toString().trim();
            int rating = (int) ratingBar.getRating();
            String reviewerName = profileN.getText().toString();
            String reviewDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            if (feedbackText.isEmpty() || rating == 0) {
                Toast.makeText(OrderHistory.this, "Please enter feedback and rating", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> feedbackImages = feedbackImagesList.get(currentFeedbackIndex);
            if (feedbackImages.size() < 3) {
                Toast.makeText(OrderHistory.this, "Please attach at least 3 images", Toast.LENGTH_SHORT).show();
                return;
            }
            MarketData.Feedback feedback = new MarketData.Feedback(feedbackText, rating, reviewerName, reviewDate);
            feedback.setFeedbackImages(feedbackImages); // set the feedback images
            sendButton.setVisibility(View.GONE);

            // Add feedback to the corresponding product
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Marketplace");
            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        String title = productSnapshot.child("title").getValue(String.class);
                        if (title != null && title.equals(title0)) {
                            DatabaseReference feedbackRef = productSnapshot.getRef().child("feedbackArray");

                            // Fetch existing feedback array
                            feedbackRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    List<MarketData.Feedback> feedbackList = new ArrayList<>();
                                    for (DataSnapshot feedbackSnapshot : snapshot.getChildren()) {
                                        MarketData.Feedback feedback = feedbackSnapshot.getValue(MarketData.Feedback.class);
                                        feedbackList.add(feedback);
                                    }

                                    // Add new feedback
                                    MarketData.Feedback newFeedback = new MarketData.Feedback(feedbackText, rating, reviewerName, reviewDate);
                                    newFeedback.setFeedbackImages(feedbackImagesList.get(currentFeedbackIndex)); // Set the feedback images
                                    feedbackList.add(newFeedback);

                                    // Save back the feedback list
                                    feedbackRef.setValue(feedbackList).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(OrderHistory.this, "Feedback sent", Toast.LENGTH_SHORT).show();
                                            feedbackEditText.setText("");
                                            feedbackEditText.setEnabled(false);
                                            ratingBar.setRating(0);
                                            ratingBar.setEnabled(false);
                                            sendButton.setEnabled(false);

                                            Intent intent = new Intent(OrderHistory.this, MainActivity.class);
                                            intent.putExtra("menuFragment", true);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(OrderHistory.this, "Failed to send feedback", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(OrderHistory.this, "Failed to retrieve feedback", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(OrderHistory.this, "Failed to send feedback", Toast.LENGTH_SHORT).show();
                }
            });
        });

        itemLayout.addView(headerLayout);
        itemLayout.addView(feedbackEditText);
        itemLayout.addView(ratingBar);
        itemLayout.addView(imageLayout); // Add image layout with a unique ID
        itemLayout.addView(addImageButton);
        itemLayout.addView(textView);
        itemLayout.addView(sendButton);

        feedbackImagesList.add(new ArrayList<>());
        // Store the imageLayoutId and feedback index in the tag of itemLayout for later use
        itemLayout.setTag(TAG_IMAGE_LAYOUT_ID, imageLayoutId);
        itemLayout.setTag(TAG_FEEDBACK_INDEX, feedbackImagesList.size() - 1);

        return itemLayout;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null ) {
                ImageView imageView = new ImageView(OrderHistory.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        150,
                        150
                );
                params.setMargins(10, 10, 10, 10);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageURI(selectedImageUri);

                LinearLayout itemLayout = (LinearLayout) itemLay2.getChildAt(currentFeedbackIndex);
                int imageLayoutId = (int) itemLayout.getTag(TAG_IMAGE_LAYOUT_ID); // Get the stored imageLayoutId
                LinearLayout imageLayout = itemLayout.findViewById(imageLayoutId); // Find imageLayout by ID

                if (imageLayout != null) {
                    imageLayout.addView(imageView);
                }

                uploadImage(selectedImageUri);
            }
        }
    }

    private boolean isViewAttached() {
        return getWindow() != null && getWindow().getDecorView() != null;
    }

    private void uploadImage(Uri imageUri) {
        String imageName = UUID.randomUUID().toString();
        StorageReference imageReference = storageReference.child("feedback_images/" + imageName);
        imageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    List<String> feedbackImages = feedbackImagesList.get(currentFeedbackIndex);
                    feedbackImages.add(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(OrderHistory.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
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