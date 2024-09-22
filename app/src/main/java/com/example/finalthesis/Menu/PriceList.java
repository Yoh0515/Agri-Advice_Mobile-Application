package com.example.finalthesis.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.finalthesis.Marketplace.AddToCart;
import com.example.finalthesis.Marketplace.Product_Detail;
import com.example.finalthesis.R;

public class PriceList extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Price List");

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