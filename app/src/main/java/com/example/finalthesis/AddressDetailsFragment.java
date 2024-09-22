package com.example.finalthesis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddressDetailsFragment extends Fragment {

    EditText blk, lot, subdivision, city, postalCode;
    Spinner barangaySpinner;
    Button backButton, nextButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_details, container, false);

        blk = view.findViewById(R.id.blk);
        lot = view.findViewById(R.id.lot);
        subdivision = view.findViewById(R.id.subdivision);
        city = view.findViewById(R.id.city);
        barangaySpinner = view.findViewById(R.id.brgy_spinner);
        postalCode = view.findViewById(R.id.postalCode);
        backButton = view.findViewById(R.id.backButton);
        nextButton = view.findViewById(R.id.nextButton);

        // Populate the barangay spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.barangay_array, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barangaySpinner.setAdapter(adapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    ((RegisterModule) getActivity()).setAddressDetails(
                            blk.getText().toString(),
                            lot.getText().toString(),
                            subdivision.getText().toString(),
                            city.getText().toString(),
                            postalCode.getText().toString(),
                            barangaySpinner.getSelectedItem().toString()
                    );
                    ((RegisterModule) getActivity()).loadFragment(new AccountDetailsFragment(), true);
                }
            }
        });

        return view;
    }

    private boolean validateFields() {
        if (blk.getText().toString().isEmpty() ||
                lot.getText().toString().isEmpty() ||
                subdivision.getText().toString().isEmpty() ||
                city.getText().toString().isEmpty() ||
                barangaySpinner.getSelectedItem().toString().isEmpty() ||
                postalCode.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
