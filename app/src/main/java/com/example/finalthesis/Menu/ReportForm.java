package com.example.finalthesis.Menu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.finalthesis.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportForm extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String threadId;
    String currentUserEmail;
    String currentUserName;
    String adminEmail;
    String adminName;
    String userId;
    Toolbar toolbar;
    LinearLayout attachment, selectedImagesLayout;
    TextView messageReport, senderName;
    Button sendReports;
    AutoCompleteTextView subjectCategory;
    String[] items = {"Pest", "Disease", "others"};
    ArrayAdapter<String> subjectAdapter;
    private static final int MAX_IMAGE_COUNT = 5;
    private List<Uri> selectedImages;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 12;
    private static final Random RANDOM = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        attachment = findViewById(R.id.attachment);
        selectedImagesLayout = findViewById(R.id.selectedImagesLayout);
        messageReport = findViewById(R.id.reportMessage);
        sendReports = findViewById(R.id.sendReport);
        subjectCategory = findViewById(R.id.reportSubject);
        senderName = findViewById(R.id.sendername);


        subjectAdapter = new ArrayAdapter<>(this, R.layout.item_category, items);
        subjectCategory.setAdapter(subjectAdapter);

        selectedImages = new ArrayList<>();

        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImages.size() < MAX_IMAGE_COUNT) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    someActivityResultLauncher.launch(intent);
                } else {
                    // Show message: Max 5 images
                }
            }
        });

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        if (result.getData().getClipData() != null) {
                            int count = result.getData().getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                if (selectedImages.size() < MAX_IMAGE_COUNT) {
                                    Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                                    selectedImages.add(imageUri);
                                    displaySelectedImages();
                                }
                            }
                        } else if (result.getData().getData() != null) {
                            Uri imageUri = result.getData().getData();
                            selectedImages.add(imageUri);
                            displaySelectedImages();
                        }
                    }
                }
        );

        sendReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                Intent intent = new Intent(ReportForm.this, Reports_Module.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        currentUserEmail = fAuth.getCurrentUser().getEmail();
        adminEmail = "agriadvice123@gmail.com";
        threadId = generateThreadId(currentUserEmail, adminEmail);


        ExecutorService executorService = Executors.newFixedThreadPool(1);
        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(executorService, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    String fullname = value.getString("Fullname");
                    if (fullname != null) {
                        // Update UI on the main thread
                        ReportForm.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                senderName.setText(fullname);
                            }
                        });
                    }
                }
            }
        });

    }
    private String generateThreadId(String email1, String email2) {
        if (email1.compareTo(email2) < 0) {
            return email1 + "_" + email2;
        } else {
            return email2 + "_" + email1;
        }
    }

    private void sendMessage() {
        String messageText = messageReport.getText().toString().trim();
        String subjectText = subjectCategory.getText().toString().trim();
        String userFullName = senderName.getText().toString().trim();

        if (!messageText.isEmpty()) {
            Map<String, Object> message = new HashMap<>();
            message.put("sender", currentUserEmail);
            message.put("receiver", adminEmail);
            message.put("senderName", userFullName);
            message.put("receiverName", adminName);
            message.put("isResolved", "Unresolved");
            message.put("message", messageText);
            message.put("timestamp", System.currentTimeMillis());
            message.put("subject", subjectText);
            generateUniqueChatReportId(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String reportId) {
                    message.put("reportId", reportId);
                    sendOrUploadMessage(message);
                }
            });

        } else {
            // Handle empty message case (show a toast or a dialog)
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendOrUploadMessage(Map<String, Object> message) {
        DocumentReference threadRef = fStore.collection("Complaints").document(threadId);
        uploadImagesToFirebase(message, threadRef);
    }

    private void uploadImagesToFirebase(Map<String, Object> message, DocumentReference threadRef) {
        List<String> imageUrls = new ArrayList<>();
        for (Uri imageUri : selectedImages) {
            // Generate a unique file name for each image
            String fileName = System.currentTimeMillis() + ".jpg";
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("chat_images_complains/" + threadId + "/" + fileName);

            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                imageUrls.add(uri.toString());
                if (imageUrls.size() == selectedImages.size()) {
                    // All images uploaded
                    message.put("images", imageUrls);
                    sendMessageToFirestore(message, threadRef);
                }
            }));
        }
    }

    private void sendMessageToFirestore(Map<String, Object> message, DocumentReference threadRef) {
        threadRef.collection("Chats").add(message);
        threadRef.update("participants", Arrays.asList(currentUserEmail, adminEmail))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        messageReport.setText("");
                        selectedImages.clear();
                        selectedImagesLayout.removeAllViews();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        generateUniqueChatReportId(id -> {
                            Map<String, Object> threadData = new HashMap<>();
                            Map<String, String> actionsTaken = new HashMap<>();
                            actionsTaken.put("actionTaken", "");
                            actionsTaken.put("dateTimeResolved", "");
                            actionsTaken.put("daysBeforeResolved", "");
                            actionsTaken.put("firstChatAdminDate", "");

                            threadData.put("participants", Arrays.asList(currentUserEmail, adminEmail));
                            threadData.put("isResolved", "Unresolved");
                            threadData.put("actionsTaken", actionsTaken);
                            threadData.put("sentTimestamp", System.currentTimeMillis());
                            threadData.put("reportId", id);

                            threadRef.set(threadData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            messageReport.setText("");
                                            selectedImages.clear();
                                            selectedImagesLayout.removeAllViews();
                                        }
                                    });
                        });
                    }
                });
    }

    private void generateUniqueChatReportId(OnSuccessListener<String> onSuccessListener) {
        String reportId = generateRandomId();
        fStore.collection("Complaints")
                .whereEqualTo("reportId", reportId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().isEmpty()) {
                        onSuccessListener.onSuccess(reportId);
                    } else {
                        // If the generated reportId already exists, generate a new one
                        generateUniqueChatReportId(onSuccessListener);
                    }
                });
    }

    private String generateRandomId() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private void displaySelectedImages() {
        selectedImagesLayout.removeAllViews();
        for (Uri imageUri : selectedImages) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            imageView.setImageURI(imageUri);
            selectedImagesLayout.addView(imageView);
        }
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