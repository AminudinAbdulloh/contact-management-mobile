package com.example.contactmanagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.contactmanagement.models.ApiResponse;
import com.example.contactmanagement.models.UpdateUserRequest;
import com.example.contactmanagement.models.User;
import com.example.contactmanagement.utils.DialogHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {

    private LinearLayout llBackToContacts;
    private EditText etFullName, etNewPassword, etConfirmPassword;
    private Button btnUpdateProfile, btnUpdatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        checkAuthentication();

        initViews();
        setupListeners();
        loadUserProfile();
    }

    private void initViews() {
        llBackToContacts = findViewById(R.id.llBackToProfile);
        etFullName = findViewById(R.id.etFullName);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
    }

    private void setupListeners() {
        llBackToContacts.setOnClickListener(v -> finish());
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
        btnUpdatePassword.setOnClickListener(v -> updatePassword());
    }

    private void loadUserProfile() {
        String token = sharedPrefManager.getToken();

        apiService.getCurrentUser(token).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().data;
                    if (user != null) {
                        etFullName.setText(user.name);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String fullName = etFullName.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }

        DialogHelper.showLoadingDialog(this, "Updating profile...");
        btnUpdateProfile.setEnabled(false);

        UpdateUserRequest request = new UpdateUserRequest();
        request.name = fullName;

        String token = sharedPrefManager.getToken();
        String username = sharedPrefManager.getUsername();

        apiService.updateUser(token, request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                DialogHelper.dismissLoadingDialog();
                btnUpdateProfile.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().data;
                    if (user != null) {
                        // Update saved user info - KEEP THE EXISTING TOKEN
                        // API tidak mengembalikan token, jadi kita pakai token yang sudah ada
                        sharedPrefManager.saveUser(token, username, user.name);

                        DialogHelper.showSuccessDialog(
                                ProfileActivity.this,
                                "Profile updated successfully!",
                                null
                        );
                    }
                } else {
                    String errorMessage = "Failed to update profile";
                    if (response.body() != null && response.body().errors != null) {
                        errorMessage = response.body().errors;
                    }
                    DialogHelper.showErrorDialog(ProfileActivity.this, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                DialogHelper.dismissLoadingDialog();
                btnUpdateProfile.setEnabled(true);
                DialogHelper.showFailureDialog(ProfileActivity.this, t);
            }
        });
    }

    private void updatePassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty()) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        DialogHelper.showLoadingDialog(this, "Updating password...");
        btnUpdatePassword.setEnabled(false);

        UpdateUserRequest request = new UpdateUserRequest();
        request.password = newPassword;

        String token = sharedPrefManager.getToken();

        apiService.updateUser(token, request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                DialogHelper.dismissLoadingDialog();
                btnUpdatePassword.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().data;
                    if (user != null) {
                        // Password berhasil diupdate
                        // Token tetap sama, tidak perlu update

                        // Clear password fields
                        etNewPassword.setText("");
                        etConfirmPassword.setText("");

                        DialogHelper.showSuccessDialog(
                                ProfileActivity.this,
                                "Password updated successfully!",
                                null
                        );
                    }
                } else {
                    String errorMessage = "Failed to update password";
                    if (response.body() != null && response.body().errors != null) {
                        errorMessage = response.body().errors;
                    }
                    DialogHelper.showErrorDialog(ProfileActivity.this, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                DialogHelper.dismissLoadingDialog();
                btnUpdatePassword.setEnabled(true);
                DialogHelper.showFailureDialog(ProfileActivity.this, t);
            }
        });
    }
}