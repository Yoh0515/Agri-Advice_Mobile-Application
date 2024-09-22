package com.example.finalthesis.Fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.PestandDisease.AdapterDisease;
import com.example.finalthesis.PestandDisease.AdapterPest;
import com.example.finalthesis.PestandDisease.Data_Disease;
import com.example.finalthesis.PestandDisease.Data_Pest;
import com.example.finalthesis.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class PestsAndDisease extends Fragment {

    RecyclerView recyclerView,recyclerView2;
    SearchView searchView;
    FirebaseFirestore firestore;
    CollectionReference pestCollection,diseaseCollection;
    DatabaseReference databaseReference,databaseReference2;
    AdapterPest pestAdapter;
    AdapterDisease diseaseAdapater;
    ArrayList<Data_Disease> DiseaseList;
    ArrayList<Data_Pest> PestList;
    Button bPests, bDisease;
    LinearLayout Lpest, Ldisease;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pestanddisease, container, false);

        int numberOfColumns = 2;
        databaseReference = FirebaseDatabase.getInstance().getReference("Pests");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Disease");
        firestore = FirebaseFirestore.getInstance();
        pestCollection = firestore.collection("Pest Pdfs");
        diseaseCollection = firestore.collection("Disease Pdfs");
        bPests = view.findViewById(R.id.bPests);
        bDisease = view.findViewById(R.id.bDisease);
        Lpest = view.findViewById(R.id.Lpests);
        Ldisease = view.findViewById(R.id.Ldisease);
        recyclerView = view.findViewById(R.id.recycleView0);
        recyclerView2 = view.findViewById(R.id.recycleView10);
        recyclerView.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),numberOfColumns));
        recyclerView2.setLayoutManager(new GridLayoutManager(getContext(),numberOfColumns));
        PestList = new ArrayList<>();
        DiseaseList = new ArrayList<>();
        pestAdapter = new AdapterPest(requireContext(), PestList);
        diseaseAdapater = new AdapterDisease(requireContext(), DiseaseList);
        recyclerView.setAdapter(pestAdapter);
        recyclerView2.setAdapter(diseaseAdapater);
        searchView = view.findViewById(R.id.search1);
        searchView.clearFocus();



        pestCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                PestList.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Data_Pest pestData = documentSnapshot.toObject(Data_Pest.class);
                    if(pestData != null){
                        pestData.setKey(documentSnapshot.getId());
                        PestList.add(pestData);
                    }
                }
                pestAdapter.notifyDataSetChanged();

                Log.d("FirebaseData", "Data retrieved: " + PestList.size());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        diseaseCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DiseaseList.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Data_Disease diseaseData = documentSnapshot.toObject(Data_Disease.class);
                    if(diseaseData != null){
                        diseaseData.setKey(documentSnapshot.getId());
                        DiseaseList.add(diseaseData);
                    }
                }
                diseaseAdapater.notifyDataSetChanged();

                Log.d("FirebaseData", "Data retrieved: " + PestList.size());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        databaseReference2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                DiseaseList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    DiseaseData diseaseData = dataSnapshot.getValue(DiseaseData.class);
//                    if(diseaseData != null){
//                        diseaseData.setKey(dataSnapshot.getKey());
//                        DiseaseList.add(diseaseData);
//                    }
//                }
//                pestAdapter.notifyDataSetChanged();
//
//                Log.d("FirebaseData", "Data retrieved: " + DiseaseList.size());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

        bPests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ldisease.setVisibility(View.GONE);
                Lpest.setVisibility(View.VISIBLE);
                bPests.setBackgroundColor(Color.parseColor("#014421"));
                bPests.setTypeface(null, Typeface.BOLD);
                bDisease.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green));
                bDisease.setTypeface(null, Typeface.NORMAL);
            }
        });

        bDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Lpest.setVisibility(View.GONE);
                Ldisease.setVisibility(View.VISIBLE);
                bDisease.setBackgroundColor(Color.parseColor("#014421"));
                bDisease.setTypeface(null, Typeface.BOLD);
                bPests.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green));
                bPests.setTypeface(null, Typeface.NORMAL);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                searchList1(newText);
                return true;
            }
        });


        return view;
    }

    public void searchList(String text){
        ArrayList<Data_Pest> searchList = new ArrayList<>();
        for(Data_Pest pestData: PestList){
            if(pestData.getPlantTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(pestData);
            }
        }
        pestAdapter.searchDataList(searchList);
    }

    public void searchList1(String text){
        ArrayList<Data_Disease> searchList = new ArrayList<>();
        for(Data_Disease diseaseData: DiseaseList){
            if(diseaseData.getPlantTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(diseaseData);
            }
        }
        diseaseAdapater.searchDataList(searchList);
    }
}