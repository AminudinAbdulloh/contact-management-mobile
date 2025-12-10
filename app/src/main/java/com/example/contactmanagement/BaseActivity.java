package com.example.contactmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contactmanagement.api.ApiClient;
import com.example.contactmanagement.api.ApiService;
import com.example.contactmanagement.models.ApiResponse;
import com.example.contactmanagement.utils.DialogHelper;
import com.example.contactmanagement.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Base Activity that handles common functionality across all activities
 * like header navigation (Profile, Logout)
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected ApiService apiService;
    protected SharedPrefManager sharedPrefManager;

    private LinearLayout llProfile;
    private LinearLayout llLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize common services
        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPrefManager = SharedPrefManager.getInstance(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        // Setup header listeners after layout is set
        setupHeaderListeners();
    }

    /**
     * Setup header navigation listeners (Profile & Logout)
     * This method is called automatically after setContentView
     */
    private void setupHeaderListeners() {
        llProfile = findViewById(R.id.llProfile);
        llLogout = findViewById(R.id.llLogout);

        if (llProfile != null) {
            llProfile.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfileActivity.class));
            });
        }

        if (llLogout != null) {
            llLogout.setOnClickListener(v -> showLogoutDialog());
        }
    }

    /**
     * Show logout confirmation dialog
     */
    private void showLogoutDialog() {
        DialogHelper.showConfirmationDialog(
                this,
                "Logout",
                "Are you sure you want to logout?",
                new DialogHelper.OnDialogActionListener() {
                    @Override
                    public void onPositiveClick() {
                        logout();
                    }

                    @Override
                    public void onNegativeClick() {
                        // Do nothing, dialog will dismiss
                    }
                }
        );
    }

    /**
     * Perform logout operation
     */
    private void logout() {
        String token = sharedPrefManager.getToken();
        DialogHelper.showLoadingDialog(this, "Logging out...");

        apiService.logout(token).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                DialogHelper.dismissLoadingDialog();
                handleLogoutSuccess();
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                DialogHelper.dismissLoadingDialog();
                handleLogoutSuccess(); // Still logout even if API fails
            }
        });
    }

    /**
     * Handle successful logout
     */
    private void handleLogoutSuccess() {
        sharedPrefManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Check if user is logged in, redirect to login if not
     * Call this in onCreate of activities that require authentication
     */
    protected void checkAuthentication() {
        if (!sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}