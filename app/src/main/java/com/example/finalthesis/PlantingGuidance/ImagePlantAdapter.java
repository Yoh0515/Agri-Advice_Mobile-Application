package com.example.finalthesis.PlantingGuidance;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.PestandDisease.DetailsDisease;
import com.example.finalthesis.PestandDisease.DetailsPest;
import com.example.finalthesis.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ImagePlantAdapter extends RecyclerView.Adapter<ImagePlantAdapter.PlantViewHolder> {
    private Context mContext;
    private List<ImageData> mPlantList;
    private List<ImageTitle> mPlantTitle;
    private CollectionReference pestCollection, diseaseCollection;
    private FirebaseFirestore firestore;

    public ImagePlantAdapter(Context context, List<ImageData> plantList, List<ImageTitle> plantTitle) {
        mContext = context;
        mPlantList = plantList;
        mPlantTitle = plantTitle;
        firestore = FirebaseFirestore.getInstance();
        pestCollection = firestore.collection("Pest Pdfs");
        diseaseCollection = firestore.collection("Disease Pdfs");
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.itemplant_image, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        if (mPlantList != null && mPlantList.size() > position) {
            ImageData plantData = mPlantList.get(position);
            Glide.with(mContext).load(plantData.getPestDiseaseUrls()).into(holder.imageView);
        }

        if (mPlantTitle != null && mPlantTitle.size() > position) {
            ImageTitle plantTitle = mPlantTitle.get(position);
            holder.title.setText(plantTitle.getPestDiseaseTitles());
        }

        String titles = "Aphids ";

        holder.itemView.setOnClickListener(view -> {

            pestCollection.whereEqualTo("plantTitle", holder.title.getText().toString()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Match found in pest collection
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            Intent intent = new Intent(mContext, DetailsPest.class);
                            intent.putExtra("Pest Title", document.getString("plantTitle"));
                            intent.putExtra("Key", document.getId());
                            intent.putExtra("Description", document.getString("description"));
                            intent.putExtra("IconUrl", document.getString("iconUrl"));
                            intent.putExtra("pdfUrl", document.getString("pdfUrl"));
                            intent.putExtra("controlMeasures", document.getString("controlMeasures"));
                            intent.putExtra("damageSymptoms", document.getString("damageSymptoms"));
                            intent.putExtra("insectCharacteristics", document.getString("insectCharacteristics"));
                            intent.putExtra("scientificName", document.getString("scientificName"));

                            // Add FLAG_ACTIVITY_NEW_TASK flag
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            // Start activity
                            mContext.startActivity(intent);
                        }
                        else {
                            diseaseCollection.whereEqualTo("plantTitle", holder.title.getText().toString()).get()
                                    .addOnCompleteListener(diseaseTask -> {
                                        if (diseaseTask.isSuccessful() && !diseaseTask.getResult().isEmpty()) {
                                            DocumentSnapshot document = diseaseTask.getResult().getDocuments().get(0);
                                            Intent intent = new Intent(mContext, DetailsDisease.class);
                                            intent.putExtra("Disease Title", document.getString("plantTitle"));
                                            intent.putExtra("Key", document.getId());
                                            intent.putExtra("Description", document.getString("description"));
                                            intent.putExtra("IconUrl", document.getString("iconUrl"));
                                            intent.putExtra("pdfUrl", document.getString("pdfUrl"));
                                            intent.putExtra("diseaseDevelopment", document.getString("diseaseDevelopment"));
                                            intent.putExtra("Symptoms", document.getString("symptoms"));
                                            intent.putExtra("culturalControl", document.getString("culturalControl"));
                                            intent.putExtra("chemicalControl", document.getString("chemicalControl"));
                                            intent.putExtra("biologicalControl", document.getString("biologicalControl"));

                                            // Add FLAG_ACTIVITY_NEW_TASK flag
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                            // Start activity
                                            mContext.startActivity(intent);
                                        } else {
                                            Toast.makeText(mContext, "No matching pest or disease found", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(mContext, "Error fetching pest data", Toast.LENGTH_SHORT).show();
                    });
        });
    }



    @Override
    public int getItemCount() {
        return Math.max(mPlantList.size(), mPlantTitle.size());
    }



    public class PlantViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);
        }
    }
}
