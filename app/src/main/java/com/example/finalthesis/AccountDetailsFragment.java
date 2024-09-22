package com.example.finalthesis;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class AccountDetailsFragment extends Fragment {

    private EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister, backButton;
    private LinearLayout progressBar;
    private ImageView showPasswordImageView, notshowPassword, showConfirmPasswordImageView, notshowConfirmPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_details, container, false);

        edtUsername = view.findViewById(R.id.username);
        edtEmail = view.findViewById(R.id.email);
        edtPassword = view.findViewById(R.id.password);
        edtConfirmPassword = view.findViewById(R.id.confirmPassword);
        btnRegister = view.findViewById(R.id.registerButton);
        progressBar = view.findViewById(R.id.progressBar);
        backButton = view.findViewById(R.id.backButton);

        showPasswordImageView = view.findViewById(R.id.showPasswordImageView);
        notshowPassword = view.findViewById(R.id.notshowPassword);
        showConfirmPasswordImageView = view.findViewById(R.id.showConfirmPasswordImageView);
        notshowConfirmPassword = view.findViewById(R.id.notshowConfirmPassword);

        showPasswordImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility((TextInputEditText) edtPassword);
            }
        });

        notshowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility((TextInputEditText) edtPassword);
            }
        });

        showConfirmPasswordImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility((TextInputEditText) edtConfirmPassword);
            }
        });

        notshowConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility((TextInputEditText) edtConfirmPassword);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String confirmPassword = edtConfirmPassword.getText().toString().trim();

                if (validateInputs(username, email, password, confirmPassword)) {
                    RegisterModule activity = (RegisterModule) getActivity();
                    if (activity != null) {
                        activity.setAccountDetails(username, email, password, confirmPassword);
                        activity.getCurrentLocationAndRegister();
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void togglePasswordVisibility(TextInputEditText passwordEditText) {
        int inputType = passwordEditText.getInputType();
        if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            notshowPassword.setVisibility(View.VISIBLE);
            showPasswordImageView.setVisibility(View.INVISIBLE);
        } else {
            passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            showPasswordImageView.setVisibility(View.VISIBLE);
            notshowPassword.setVisibility(View.INVISIBLE);
        }
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(username)) {
            edtUsername.setError("Username is required.");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email is required.");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Password is required.");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError("Confirm password is required.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Passwords do not match.");
            return false;
        }

        return true;
    }
}
