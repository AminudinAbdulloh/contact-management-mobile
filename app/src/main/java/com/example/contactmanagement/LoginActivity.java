package com.example.contactmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contactmanagement.api.ApiClient;
import com.example.contactmanagement.api.ApiService;
import com.example.contactmanagement.models.ApiResponse;
import com.example.contactmanagement.models.LoginRequest;
import com.example.contactmanagement.models.LoginResponse;
import com.example.contactmanagement.utils.DialogHelper;
import com.example.contactmanagement.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etUsername, etPassword;
    private Button btnSignIn;
    private TextView tvSignUp;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            sharedPrefManager = SharedPrefManager.getInstance(this);

            if (sharedPrefManager.isLoggedIn()) {
                startActivity(new Intent(this, ContactsActivity.class));
                finish();
                return;
            }

            setContentView(R.layout.activity_login);

            apiService = ApiClient.getClient().create(ApiService.class);
            initViews();
            setupListeners();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void setupListeners() {
        btnSignIn.setOnClickListener(v -> login());
        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        DialogHelper.showLoadingDialog(this, "Signing In...");

        btnSignIn.setEnabled(false);

        LoginRequest request = new LoginRequest();
        request.username = username;
        request.password = password;

        apiService.login(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                DialogHelper.dismissLoadingDialog();
                btnSignIn.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse data = response.body().data;
                    if (data != null) {
                        sharedPrefManager.saveUser(data.token, data.username, data.name);

                        startActivity(new Intent(LoginActivity.this, ContactsActivity.class));
                        finish();
                    } else {
                        DialogHelper.showErrorDialog(LoginActivity.this, "An error occurred. Please try again.");
                    }
                } else {
                    String errorMessage = "Invalid username or password";
                    if (response.body() != null && response.body().errors != null) {
                        errorMessage = response.body().errors;
                    }
                    DialogHelper.showErrorDialog(LoginActivity.this, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                DialogHelper.dismissLoadingDialog();
                btnSignIn.setEnabled(true);
                DialogHelper.showFailureDialog(LoginActivity.this, t);
            }
        });
    }
}