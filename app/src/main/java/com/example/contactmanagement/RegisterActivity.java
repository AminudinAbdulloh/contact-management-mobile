package com.example.contactmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contactmanagement.api.ApiClient;
import com.example.contactmanagement.api.ApiService;
import com.example.contactmanagement.models.ApiResponse;
import com.example.contactmanagement.models.RegisterRequest;
import com.example.contactmanagement.models.User;
import com.example.contactmanagement.utils.DialogHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etFullName, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvSignIn;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = ApiClient.getClient().create(ApiService.class);

        etUsername = findViewById(R.id.etUsername);
        etFullName = findViewById(R.id.etFullName);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvSignIn = findViewById(R.id.tvSignIn);

        btnRegister.setOnClickListener(v -> register());
        tvSignIn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        DialogHelper.showLoadingDialog(this, "Creating Account...");

        btnRegister.setEnabled(false);

        RegisterRequest request = new RegisterRequest();
        request.username = username;
        request.name = fullName;
        request.password = password;

        apiService.register(request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                DialogHelper.dismissLoadingDialog();
                btnRegister.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    User data = response.body().data;
                    if (data != null) {

                        DialogHelper.showSuccessDialog(
                                RegisterActivity.this,
                                "User created successfully!",
                                () -> {
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                }
                        );
                    } else {
                        DialogHelper.showErrorDialog(RegisterActivity.this, "An error occurred during registration. Please try again.");
                    }
                } else {
                    String errorMessage = "Invalid username or password";
                    if (response.body() != null && response.body().errors != null) {
                        errorMessage = response.body().errors;
                    }
                    DialogHelper.showErrorDialog(RegisterActivity.this, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                DialogHelper.dismissLoadingDialog();
                btnRegister.setEnabled(true);
                DialogHelper.showFailureDialog(RegisterActivity.this, t);
            }
        });
    }
}