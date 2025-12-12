package com.example.contactmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.contactmanagement.api.ApiClient;
import com.example.contactmanagement.api.ApiService;
import com.example.contactmanagement.models.ApiResponse;
import com.example.contactmanagement.models.Contact;
import com.example.contactmanagement.models.UpdateContactRequest;
import com.example.contactmanagement.utils.DialogHelper;
import com.example.contactmanagement.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditContactActivity extends BaseActivity {

    private LinearLayout llBackToContacts;
    private EditText etFirstName, etLastName, etEmail, etPhone;
    private Button btnCancel, btnSaveChanges;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;

    private int contactId;
    private String originalFirstName, originalLastName, originalEmail, originalPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPrefManager = SharedPrefManager.getInstance(this);

        // Get contact data from intent
        contactId = getIntent().getIntExtra("contact_id", -1);
        originalFirstName = getIntent().getStringExtra("first_name");
        originalLastName = getIntent().getStringExtra("last_name");
        originalEmail = getIntent().getStringExtra("email");
        originalPhone = getIntent().getStringExtra("phone");

        if (contactId == -1) {
            Toast.makeText(this, "Invalid contact", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        populateFields();
        setupListeners();
    }

    private void initViews() {
        llBackToContacts = findViewById(R.id.llBackToContacts);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnCancel = findViewById(R.id.btnCancel);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
    }

    private void populateFields() {
        etFirstName.setText(originalFirstName);
        etLastName.setText(originalLastName);
        etEmail.setText(originalEmail);
        etPhone.setText(originalPhone);
    }

    private void setupListeners() {
        llBackToContacts.setOnClickListener(v -> {
            startActivity(new Intent(this, ContactsActivity.class));
        });
        btnCancel.setOnClickListener(v -> finish());
        btnSaveChanges.setOnClickListener(v -> updateContact());
    }

    private void updateContact() {
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

        DialogHelper.showLoadingDialog(EditContactActivity.this, "Updating contact...");

        btnSaveChanges.setEnabled(false);

        UpdateContactRequest request = new UpdateContactRequest();
        request.firstName = firstName;
        request.lastName = lastName;
        request.email = email;
        request.phone = phone;

        String token = sharedPrefManager.getToken();

        apiService.updateContact(token, String.valueOf(contactId), request)
                .enqueue(new Callback<ApiResponse<Contact>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Contact>> call, Response<ApiResponse<Contact>> response) {
                        DialogHelper.dismissLoadingDialog();
                        btnSaveChanges.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            DialogHelper.showSuccessDialog(
                                    EditContactActivity.this,
                                    "Contact updated successfully!",
                                    () -> finish()
                            );
                        } else {
                            String errorMessage = "Failed to update contact";
                            if (response.body() != null && response.body().errors != null) {
                                errorMessage = response.body().errors;
                            }
                            DialogHelper.showErrorDialog(EditContactActivity.this, errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Contact>> call, Throwable t) {
                        DialogHelper.dismissLoadingDialog();
                        btnSaveChanges.setEnabled(true);
                        DialogHelper.showFailureDialog(EditContactActivity.this, t);
                    }
                });
    }
}