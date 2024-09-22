package com.example.finalthesis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalthesis.Fragment.Home;
import com.example.finalthesis.Fragment.Marketplace;
import com.example.finalthesis.Fragment.Menu;
import com.example.finalthesis.Fragment.PestsAndDisease;
import com.example.finalthesis.Fragment.Planting_Guidance;
import com.example.finalthesis.Fragment.Weather_Updates;
import com.example.finalthesis.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private boolean exitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new Home());



        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();


            if (itemId == R.id.home) {
                replaceFragment(new Home());
            } else if (itemId == R.id.pg) {
                replaceFragment(new Planting_Guidance());
            } else if (itemId == R.id.wu) {
                replaceFragment(new PestsAndDisease());
            } else if (itemId == R.id.mp) {
                replaceFragment(new Marketplace());
            }else if (itemId == R.id.menu) {
                replaceFragment(new Menu()  );
            }
            return  true;
        });

        Intent intent = getIntent();
        boolean menuFragmentRequested = intent.getBooleanExtra("menuFragment", false);
        boolean marketFragmentRequested = intent.getBooleanExtra("marketFragment", false);
        boolean weatherFragmentRequested = intent.getBooleanExtra("weatherFragment", false);
        if (menuFragmentRequested) {
            replaceFragment(new Menu());
            binding.bottomNavigation.setSelectedItemId(R.id.menu);
        } else if (marketFragmentRequested) {
            replaceFragment(new Marketplace());
            binding.bottomNavigation.setSelectedItemId(R.id.mp);
        }else if (weatherFragmentRequested) {
            replaceFragment(new Weather_Updates());
            binding.bottomNavigation.setSelectedItemId(R.id.wu);
        }


    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (exitPressedOnce) {
            showExitDialog();
        } else {
            exitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitPressedOnce = false;
                }
            }, 2000); // Reset the variable after 2 seconds
        }
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}