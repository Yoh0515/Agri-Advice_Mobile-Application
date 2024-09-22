package com.example.finalthesis.Marketplace;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.finalthesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SeminarForms extends AppCompatActivity {

    Toolbar toolbar;
    EditText email, fullName, address, phoneNumber, organization, positionOrganization, titleTrainingSeminar, suggestedVenue;
    RadioGroup numberOfPax;
    DatePicker targetDate;
    Button sendForms;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seminar_forms);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrowback);
        int titleTextColor = getResources().getColor(android.R.color.white);
        toolbar.setTitleTextColor(titleTextColor);
        getSupportActionBar().setTitle("Seminar Forms");

        email = findViewById(R.id.email);
        fullName = findViewById(R.id.full_name);
        address = findViewById(R.id.address);
        phoneNumber = findViewById(R.id.phone_number);
        organization = findViewById(R.id.organization);
        positionOrganization = findViewById(R.id.position_organization);
        titleTrainingSeminar = findViewById(R.id.title_training_seminar);
        numberOfPax = findViewById(R.id.number_of_pax);
        targetDate = findViewById(R.id.target_date);
        suggestedVenue = findViewById(R.id.suggested_venue);
        sendForms = findViewById(R.id.send_forms);

        // Prefill the fields with the received data
        email.setText(getIntent().getStringExtra("CURRENT_USER_EMAIL"));
        fullName.setText(getIntent().getStringExtra("CURRENT_USER_NAME"));
        address.setText(getIntent().getStringExtra("CURRENT_USER_ADDRESS"));
        phoneNumber.setText(getIntent().getStringExtra("CURRENT_USER_NUMBER"));

        sendForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userFullName = fullName.getText().toString();
                String userAddress = address.getText().toString();
                String userPhoneNumber = phoneNumber.getText().toString();
                String userOrganization = organization.getText().toString();
                String userPositionOrganization = positionOrganization.getText().toString();
                String userTitleTrainingSeminar = titleTrainingSeminar.getText().toString();

                int selectedPaxId = numberOfPax.getCheckedRadioButtonId();
                String userNumberOfPax = ((RadioButton) findViewById(selectedPaxId)).getText().toString();

                int day = targetDate.getDayOfMonth();
                int month = targetDate.getMonth() + 1; // Month is 0-indexed
                int year = targetDate.getYear();
                String userTargetDate = day + "/" + month + "/" + year;

                String userSuggestedVenue = suggestedVenue.getText().toString();

                // Generate PDF
                String pdfPath = generatePdf(SeminarForms.this, userEmail, userFullName, userAddress, userPhoneNumber, userOrganization, userPositionOrganization, userTitleTrainingSeminar, userNumberOfPax, userTargetDate, userSuggestedVenue);

                // Launch MessagesAdminSeminar with PDF path
                launchMessagesAdminSeminar(pdfPath);
            }
        });
    }

    private String generatePdf(Context context, String email, String fullName, String address, String phoneNumber, String organization, String positionOrganization, String titleTrainingSeminar, String numberOfPax, String targetDate, String suggestedVenue) {
        PdfDocument document = new PdfDocument();
        // Set page size for landscape
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(792, 612, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Paint paint = new Paint();
        paint.setColor(android.graphics.Color.BLACK);
        paint.setTextSize(12);

        Paint titlePaint = new Paint();
        titlePaint.setColor(android.graphics.Color.BLACK);
        titlePaint.setTextSize(20);
        titlePaint.setFakeBoldText(true);

        // Draw title centered
        int pageWidth = pageInfo.getPageWidth();
        String title = "Seminar Request Form";
        float titleWidth = titlePaint.measureText(title);
        int x = (pageWidth - (int) titleWidth) / 2;
        int y = 50;

        page.getCanvas().drawText(title, x, y, titlePaint);
        y += 50;

        // Draw form fields
        x = 50;
        int lineHeight = 40;
        page.getCanvas().drawText("Email: " + email, x, y, paint);
        y += lineHeight;
        page.getCanvas().drawText("Full Name: " + fullName, x, y, paint);
        y += lineHeight;
        page.getCanvas().drawText("Address: " + address, x, y, paint);
        y += lineHeight;
        page.getCanvas().drawText("Phone Number: " + phoneNumber, x, y, paint);
        y += lineHeight;
        page.getCanvas().drawText("Organization: " + organization, x, y, paint);
        y += lineHeight;
        page.getCanvas().drawText("Position in Organization: " + positionOrganization, x, y, paint);
        y += lineHeight;
        page.getCanvas().drawText("Title of Training/Seminar/Lecture Requested: " + titleTrainingSeminar, x, y, paint);
        y += lineHeight;
        page.getCanvas().drawText("Number of Pax: " + numberOfPax, x, y, paint);
        y += lineHeight;
        page.getCanvas().drawText("Target Date: " + targetDate, x, y, paint);
        y += lineHeight;
        page.getCanvas().drawText("Suggested Venue: " + suggestedVenue, x, y, paint);
        y += lineHeight;

        // Draw footer
        y = pageInfo.getPageHeight() - 40;
        page.getCanvas().drawText("Sent by: " + fullName, x, y, paint);

        document.finishPage(page);

        // Save the PDF to external storage
        File pdfDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "SeminarRequests");
        if (!pdfDir.exists()) {
            pdfDir.mkdir();
        }
        File pdfFile = new File(pdfDir, "seminar_request_" + System.currentTimeMillis() + ".pdf");
        try {
            document.writeTo(new FileOutputStream(pdfFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();

        return pdfFile.getAbsolutePath();
    }

    private void launchMessagesAdminSeminar(String pdfPath) {
        Intent intent = new Intent(SeminarForms.this, MessagesAdminSeminar.class);
        intent.putExtra("THREAD_ID", getIntent().getStringExtra("THREAD_ID"));
        intent.putExtra("CURRENT_USER_EMAIL", getIntent().getStringExtra("CURRENT_USER_EMAIL"));
        intent.putExtra("ADMIN_EMAIL", getIntent().getStringExtra("ADMIN_EMAIL"));
        intent.putExtra("CURRENT_USER_NAME", getIntent().getStringExtra("CURRENT_USER_NAME"));
        intent.putExtra("ADMIN_NAME", getIntent().getStringExtra("ADMIN_NAME"));
        intent.putExtra("PDF_PATH", pdfPath);
        startActivity(intent);
    }
}