package com.example.contactmanagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contactmanagement.api.ApiClient;
import com.example.contactmanagement.api.ApiService;
import com.example.contactmanagement.models.Address;
import com.example.contactmanagement.models.ApiResponse;
import com.example.contactmanagement.models.Contact;
import com.example.contactmanagement.models.CreateAddressRequest;
import com.example.contactmanagement.utils.DialogHelper;
import com.example.contactmanagement.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {

    private LinearLayout llBackToContactDetails;
    private TextView tvContactName, tvContactEmail;
    private EditText etStreet, etCity, etProvince, etCountry, etPostalCode;
    private Button btnCancel, btnSaveChanges;

    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;
    private int contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPrefManager = SharedPrefManager.getInstance(this);

        // Get contact ID from intent
        contactId = getIntent().getIntExtra("contact_id", -1);

        if (contactId == -1) {
            Toast.makeText(this, "Invalid contact", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
        loadContactInfo();
    }

    private void initViews() {
        llBackToContactDetails = findViewById(R.id.llBackToContactDetails);
        tvContactName = findViewById(R.id.tvContactName);
        tvContactEmail = findViewById(R.id.tvContactEmail);
        etStreet = findViewById(R.id.etStreet);
        etCity = findViewById(R.id.etCity);
        etProvince = findViewById(R.id.etProvince);
        etCountry = findViewById(R.id.etCountry);
        etPostalCode = findViewById(R.id.etPostalCode);
        btnCancel = findViewById(R.id.btnCancel);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
    }

    private void setupListeners() {
        llBackToContactDetails.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnSaveChanges.setOnClickListener(v -> addAddress());
    }

    private void loadContactInfo() {
        String token = sharedPrefManager.getToken();

        apiService.getContact(token, String.valueOf(contactId))
                .enqueue(new Callback<ApiResponse<Contact>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Contact>> call, Response<ApiResponse<Contact>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Contact contact = response.body().data;
                            if (contact != null) {
                                displayContactInfo(contact);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Contact>> call, Throwable t) {
                        Toast.makeText(AddAddressActivity.this,
                                "Failed to load contact info", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayContactInfo(Contact contact) {
        String fullName = contact.firstName + " " + contact.lastName;
        tvContactName.setText(fullName);
        tvContactEmail.setText(contact.email);
    }

    private void addAddress() {
        String street = etStreet.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String province = etProvince.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String postalCode = etPostalCode.getText().toString().trim();

        // Validation
        if (street.isEmpty()) {
            etStreet.setError("Street is required");
            etStreet.requestFocus();
            return;
        }

        if (city.isEmpty()) {
            etCity.setError("City is required");
            etCity.requestFocus();
            return;
        }

        if (province.isEmpty()) {
            etProvince.setError("Province is required");
            etProvince.requestFocus();
            return;
        }

        if (country.isEmpty()) {
            etCountry.setError("Country is required");
            etCountry.requestFocus();
            return;
        }

        if (postalCode.isEmpty()) {
            etPostalCode.setError("Postal code is required");
            etPostalCode.requestFocus();
            return;
        }

        DialogHelper.showLoadingDialog(this, "Adding address...");
        btnSaveChanges.setEnabled(false);

        CreateAddressRequest request = new CreateAddressRequest();
        request.street = street;
        request.city = city;
        request.province = province;
        request.country = country;
        request.postalCode = postalCode;

        String token = sharedPrefManager.getToken();

        apiService.createAddress(token, String.valueOf(contactId), request)
                .enqueue(new Callback<ApiResponse<Address>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Address>> call, Response<ApiResponse<Address>> response) {
                        DialogHelper.dismissLoadingDialog();
                        btnSaveChanges.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            DialogHelper.showSuccessDialog(
                                    AddAddressActivity.this,
                                    "Address added successfully!",
                                    () -> finish()
                            );
                        } else {
                            String errorMessage = "Failed to add address";
                            if (response.body() != null && response.body().errors != null) {
                                errorMessage = response.body().errors;
                            }
                            DialogHelper.showErrorDialog(AddAddressActivity.this, errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Address>> call, Throwable t) {
                        DialogHelper.dismissLoadingDialog();
                        btnSaveChanges.setEnabled(true);
                        DialogHelper.showFailureDialog(AddAddressActivity.this, t);
                    }
                });
    }
}