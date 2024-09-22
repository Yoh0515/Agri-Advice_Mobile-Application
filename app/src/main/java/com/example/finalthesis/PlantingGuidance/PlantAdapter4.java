package com.example.finalthesis.PlantingGuidance;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.Marketplace.MarketData;
import com.example.finalthesis.PestandDisease.DiseaseData;
import com.example.finalthesis.PestandDisease.DiseaseDetails;
import com.example.finalthesis.PestandDisease.PestData;
import com.example.finalthesis.PestandDisease.PestDetails;
import com.example.finalthesis.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PlantAdapter4 extends RecyclerView.Adapter<PlantAdapter4.MyViewHolder> {

    Context context;
    ArrayList<PlantData3> PlantList;

    public PlantAdapter4(Context context, ArrayList<PlantData3> plantList) {
        this.context = context;
        this.PlantList = plantList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plant3,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return PlantList.size();
    }

    public void searchDataList(ArrayList<PlantData3> searchList){
        PlantList = searchList;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context).load(PlantList.get(position).getIconUrl()).into(holder.iconImage);
        holder.titleTextView.setText(PlantList.get(position).getTitle());

        holder.pItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fetch pest data from Firebase Realtime Database
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Disease");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Iterate through the pest data and handle it
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DiseaseData diseaseData = snapshot.getValue(DiseaseData.class);

                            // Compare titleTextView value with pestData.title
                            if (holder.titleTextView.getText().toString().equals(diseaseData.getTitle())) {
                                // If they match, create an Intent and start PestDetails activity
                                Intent intent = new Intent(context, DiseaseDetails.class);
                                intent.putExtra("Image", diseaseData.getImageUrl());
                                intent.putExtra("Identify", diseaseData.getIdentify());
                                intent.putExtra("Chemical", diseaseData.getChemical());
                                intent.putExtra("Damage", diseaseData.getDamage());
                                intent.putExtra("Physical", diseaseData.getPhysical());
                                intent.putExtra("Title", diseaseData.getTitle());
                                intent.putExtra("Key", diseaseData.getKey());

                                // Add FLAG_ACTIVITY_NEW_TASK flag
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                // Start activity
                                context.startActivity(intent);
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors that occur
                        Log.e("FirebaseDatabase", "Error fetching pest data", databaseError.toException());
                        Toast.makeText(context, "Error fetching pest data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout pItem;
        ImageView iconImage;
        TextView titleTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pItem = itemView.findViewById(R.id.pItem2);
            iconImage = itemView.findViewById(R.id.icon_image);
            titleTextView = itemView.findViewById(R.id.title_text_view);
        }
    }
}
