package com.example.finalthesis;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UserDetailsFragment extends Fragment {

    EditText firstName, lastName, middleName, number;
    Button nextButton;

    TextView toLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);

        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        middleName = view.findViewById(R.id.middleName);
        number = view.findViewById(R.id.number);
        nextButton = view.findViewById(R.id.nextButton);
        toLogin = view.findViewById(R.id.toLogin);

        InputFilter lengthFilter = new InputFilter.LengthFilter(11);
        InputFilter digitFilter = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        };
        number.setFilters(new InputFilter[]{lengthFilter, digitFilter});


        number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) { // When focus is lost
                    String input = number.getText().toString();
                    if (input.length() != 11) {
                        Toast.makeText(number.getContext(), "Phone Number must be exactly 11 digits", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    ((RegisterModule) getActivity()).setUserDetails(
                            firstName.getText().toString(),
                            lastName.getText().toString(),
                            middleName.getText().toString(),
                            number.getText().toString()
                    );
                    ((RegisterModule) getActivity()).loadFragment(new AddressDetailsFragment(), true);
                }
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Login.class));
            }
        });


        return view;
    }

    private boolean validateFields() {
        if (firstName.getText().toString().isEmpty() ||
                lastName.getText().toString().isEmpty() ||
               number.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        String phoneNumber = number.getText().toString();
        if (phoneNumber.length() != 11) {
            Toast.makeText(getActivity(), "Phone Number must be exactly 11 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
