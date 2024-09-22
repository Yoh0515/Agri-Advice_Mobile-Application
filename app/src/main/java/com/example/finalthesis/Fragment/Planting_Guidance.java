package com.example.finalthesis.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalthesis.PlantingGuidance.AdapterPlant;
import com.example.finalthesis.PlantingGuidance.DataPlant;
import com.example.finalthesis.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Planting_Guidance extends Fragment {

    RecyclerView recyclerView;
    SearchView searchView;
    ImageView filter;
    FirebaseFirestore firestore;
    CollectionReference plantCollection;
    AdapterPlant plantAdapter;
    ArrayList<DataPlant> PlantList;
    String selectedCategory;
    LinearLayout filter_item;
    FrameLayout frameLayout;
    boolean isFilterItemVisible = false;
    TextView all, alliums, lf, ns, fv, gb, alliums1, lf1, ns1, fv1, gb1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planting__guidance, container, false);

        all = view.findViewById(R.id.all);
        alliums = view.findViewById(R.id.alliums);
        lf = view.findViewById(R.id.lf);
        ns = view.findViewById(R.id.nightshade);
        fv = view.findViewById(R.id.fv);
        gb = view.findViewById(R.id.gb);
        alliums1 = view.findViewById(R.id.alliums1);
        lf1 = view.findViewById(R.id.lf1);
        ns1 = view.findViewById(R.id.nightshade1);
        fv1 = view.findViewById(R.id.fv1);
        gb1 = view.findViewById(R.id.gb1);
        filter_item = view.findViewById(R.id.filter_item);
        filter = view.findViewById(R.id.filter);
        frameLayout = view.findViewById(R.id.frame);
        int numberOfColumns = 2;

        firestore = FirebaseFirestore.getInstance();
        plantCollection = firestore.collection("Plant Pdfs");
        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        PlantList = new ArrayList<>();
        plantAdapter = new AdapterPlant(requireContext(), PlantList);
        recyclerView.setAdapter(plantAdapter);
        searchView = view.findViewById(R.id.search1);
        searchView.clearFocus();

        fetchPlants();

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_item.setVisibility(View.GONE);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_item.setVisibility(isFilterItemVisible ? View.GONE : View.VISIBLE);
                isFilterItemVisible = !isFilterItemVisible;
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all.setTypeface(null, Typeface.BOLD);
                alliums.setTypeface(null, Typeface.NORMAL);
                lf.setTypeface(null, Typeface.NORMAL);
                ns.setTypeface(null, Typeface.NORMAL);
                fv.setTypeface(null, Typeface.NORMAL);
                gb.setTypeface(null, Typeface.NORMAL);
                filter_item.setVisibility(View.GONE);
                alliums1.setVisibility(View.GONE);
                lf1.setVisibility(View.GONE);
                ns1.setVisibility(View.GONE);
                fv1.setVisibility(View.GONE);
                gb1.setVisibility(View.GONE);

                selectedCategory = null;
                fetchPlants();
            }
        });

        alliums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all.setTypeface(null, Typeface.NORMAL);
                alliums.setTypeface(null, Typeface.BOLD);
                lf.setTypeface(null, Typeface.NORMAL);
                ns.setTypeface(null, Typeface.NORMAL);
                fv.setTypeface(null, Typeface.NORMAL);
                gb.setTypeface(null, Typeface.NORMAL);
                filter_item.setVisibility(View.GONE);
                alliums1.setVisibility(View.VISIBLE);
                lf1.setVisibility(View.GONE);
                ns1.setVisibility(View.GONE);
                fv1.setVisibility(View.GONE);
                gb1.setVisibility(View.GONE);

                selectedCategory = "Alliums";
                fetchPlants();
            }
        });

        lf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all.setTypeface(null, Typeface.NORMAL);
                alliums.setTypeface(null, Typeface.NORMAL);
                lf.setTypeface(null, Typeface.BOLD);
                ns.setTypeface(null, Typeface.NORMAL);
                fv.setTypeface(null, Typeface.NORMAL);
                gb.setTypeface(null, Typeface.NORMAL);
                filter_item.setVisibility(View.GONE);
                alliums1.setVisibility(View.GONE);
                lf1.setVisibility(View.VISIBLE);
                ns1.setVisibility(View.GONE);
                fv1.setVisibility(View.GONE);
                gb1.setVisibility(View.GONE);

                selectedCategory = "Leafy Greens";
                fetchPlants();
            }
        });

        ns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all.setTypeface(null, Typeface.NORMAL);
                alliums.setTypeface(null, Typeface.NORMAL);
                lf.setTypeface(null, Typeface.NORMAL);
                ns.setTypeface(null, Typeface.BOLD);
                fv.setTypeface(null, Typeface.NORMAL);
                gb.setTypeface(null, Typeface.NORMAL);
                filter_item.setVisibility(View.GONE);
                alliums1.setVisibility(View.GONE);
                lf1.setVisibility(View.GONE);
                ns1.setVisibility(View.VISIBLE);
                fv1.setVisibility(View.GONE);
                gb1.setVisibility(View.GONE);

                selectedCategory = "Nightshade";
                fetchPlants();
            }
        });

        fv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all.setTypeface(null, Typeface.NORMAL);
                alliums.setTypeface(null, Typeface.NORMAL);
                lf.setTypeface(null, Typeface.NORMAL);
                ns.setTypeface(null, Typeface.NORMAL);
                fv.setTypeface(null, Typeface.BOLD);
                gb.setTypeface(null, Typeface.NORMAL);
                filter_item.setVisibility(View.GONE);
                alliums1.setVisibility(View.GONE);
                lf1.setVisibility(View.GONE);
                ns1.setVisibility(View.GONE);
                fv1.setVisibility(View.VISIBLE);
                gb1.setVisibility(View.GONE);

                selectedCategory = "Fruit Vegetables";
                fetchPlants();
            }
        });

        gb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all.setTypeface(null, Typeface.NORMAL);
                alliums.setTypeface(null, Typeface.NORMAL);
                lf.setTypeface(null, Typeface.NORMAL);
                ns.setTypeface(null, Typeface.NORMAL);
                fv.setTypeface(null, Typeface.NORMAL);
                gb.setTypeface(null, Typeface.BOLD);
                filter_item.setVisibility(View.GONE);
                alliums1.setVisibility(View.GONE);
                lf1.setVisibility(View.GONE);
                ns1.setVisibility(View.GONE);
                fv1.setVisibility(View.GONE);
                gb1.setVisibility(View.VISIBLE);

                selectedCategory = "Legumes";
                fetchPlants();
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
                return true;
            }
        });

        return view;
    }

    public void searchList(String text) {
        ArrayList<DataPlant> searchList = new ArrayList<>();
        for (DataPlant plantData : PlantList) {
            if (plantData.getPlantTitle().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(plantData);
            }
        }
        plantAdapter.searchDataList(searchList);
    }

    private void fetchPlants() {
        plantCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                PlantList.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    DataPlant plantData = documentSnapshot.toObject(DataPlant.class);
                    if (plantData != null) {
                        plantData.setKey(documentSnapshot.getId());
                        if (selectedCategory == null || selectedCategory.equals(plantData.getCategory())) {
                            PlantList.add(plantData);
                        }
                    }
                }
                plantAdapter.notifyDataSetChanged();
                Log.d("FirestoreData", "Data retrieved: " + PlantList.size());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
