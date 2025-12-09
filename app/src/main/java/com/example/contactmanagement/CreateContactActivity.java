package com.example.contactmanagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contactmanagement.api.ApiClient;
import com.example.contactmanagement.api.ApiService;
import com.example.contactmanagement.models.ApiResponse;
import com.example.contactmanagement.models.Contact;
import com.example.contactmanagement.models.CreateContactRequest;
import com.example.contactmanagement.utils.DialogHelper;
import com.example.contactmanagement.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateContactActivity extends AppCompatActivity {

    private LinearLayout llBackToContacts;
    private EditText etFirstName, etLastName, etEmail, etPhone;
    private Button btnCancel, btnCreateContact;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPrefManager = SharedPrefManager.getInstance(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        llBackToContacts = findViewById(R.id.llBackToContacts);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnCancel = findViewById(R.id.btnCancel);
        btnCreateContact = findViewById(R.id.btnCreateContact);
    }

    private void setupListeners() {
        llBackToContacts.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnCreateContact.setOnClickListener(v -> createContact());
    }

    private void createContact() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Phone is required");
            etPhone.requestFocus();
            return;
        }

        btnCreateContact.setEnabled(false);

        CreateContactRequest request = new CreateContactRequest();
        request.firstName = firstName;
        request.lastName = lastName;
        request.email = email;
        request.phone = phone;

        String token = sharedPrefManager.getToken();

        apiService.createContact(token, request).enqueue(new Callback<ApiResponse<Contact>>() {
            @Override
            public void onResponse(Call<ApiResponse<Contact>> call, Response<ApiResponse<Contact>> response) {
                btnCreateContact.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    DialogHelper.showSuccessDialog(
                            CreateContactActivity.this,
                            "Contact created successfully!",
                            () -> finish()
                    );
                } else {
                    String error = "Failed to create contact";
                    if (response.body() != null && response.body().errors != null) {
                        error = response.body().errors;
                    }
                    Toast.makeText(CreateContactActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Contact>> call, Throwable t) {
                btnCreateContact.setEnabled(true);
                Toast.makeText(CreateContactActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}