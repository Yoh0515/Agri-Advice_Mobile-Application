package com.example.finalthesis.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.finalthesis.PlantingGuidance.PlantAdapter3;
import com.example.finalthesis.PlantingGuidance.PlantAdapter4;
import com.example.finalthesis.PlantingGuidance.PlantData2;
import com.example.finalthesis.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewsUpdate extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    NewsAdapter newsAdapter;
    ArrayList<NewsData> newsData;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_update);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("News Update");

        recyclerView = findViewById(R.id.MrecycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        newsData = new ArrayList<>();
        newsAdapter = new NewsAdapter(getApplicationContext(), newsData);
        recyclerView.setAdapter(newsAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("News");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newsData.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NewsData newsData1 = dataSnapshot.getValue(NewsData.class);
                    if(newsData1 != null){
                        newsData.add(newsData1);
                    }
                }
                newsAdapter.notifyDataSetChanged();

                // Add log statement to check if data is being retrieved
                Log.d("FirebaseData", "Data retrieved: " + newsData.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar navigation back button click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // This will mimic the back button press functionality
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}