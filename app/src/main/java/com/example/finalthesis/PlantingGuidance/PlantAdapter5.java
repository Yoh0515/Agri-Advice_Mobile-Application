package com.example.finalthesis.PlantingGuidance;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlantAdapter5 extends RecyclerView.Adapter<PlantAdapter5.MyViewHolder> {

    Context context;
    ArrayList<PlantData> PlantList;

    public PlantAdapter5(Context context, ArrayList<PlantData> plantList) {
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

    public void searchDataList(ArrayList<PlantData> searchList){
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
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("plants");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            PlantData plantData = snapshot.getValue(PlantData.class);

                            // Compare titleTextView value with pestData.title
                            if (holder.titleTextView.getText().toString().equals(plantData.getTitle())) {
                                Intent intent = new Intent(context, Plant_Detail.class);
                                intent.putExtra("Description", PlantList.get(holder.getAdapterPosition()).getDescription());
                                intent.putExtra("Category", PlantList.get(holder.getAdapterPosition()).getCategory());
                                intent.putExtra("Depth", PlantList.get(holder.getAdapterPosition()).getDepth());
                                intent.putExtra("Water", PlantList.get(holder.getAdapterPosition()).getWater());
                                intent.putExtra("Sun", PlantList.get(holder.getAdapterPosition()).getSun());
                                intent.putExtra("Temp", PlantList.get(holder.getAdapterPosition()).getTemperature());
                                intent.putExtra("Grow", PlantList.get(holder.getAdapterPosition()).getGrow());
                                intent.putExtra("Plant", PlantList.get(holder.getAdapterPosition()).getPlanting());
                                intent.putExtra("Feed", PlantList.get(holder.getAdapterPosition()).getFeed());
                                intent.putExtra("Harvest", PlantList.get(holder.getAdapterPosition()).getHarvest());
                                intent.putExtra("Storage", PlantList.get(holder.getAdapterPosition()).getStorage());
                                intent.putExtra("Range", PlantList.get(holder.getAdapterPosition()).getRangeValue());
                                intent.putExtra("Title", PlantList.get(holder.getAdapterPosition()).getTitle());
                                intent.putExtra("Key", PlantList.get(holder.getAdapterPosition()).getKey());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
