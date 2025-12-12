package com.example.contactmanagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contactmanagement.api.ApiClient;
import com.example.contactmanagement.api.ApiService;
import com.example.contactmanagement.models.Address;
import com.example.contactmanagement.models.ApiResponse;
import com.example.contactmanagement.models.Contact;
import com.example.contactmanagement.models.UpdateAddressRequest;
import com.example.contactmanagement.utils.DialogHelper;
import com.example.contactmanagement.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAddressActivity extends BaseActivity {

    private LinearLayout llBackToContactDetails;
    private TextView tvContactName, tvContactEmail;
    private EditText etStreet, etCity, etProvince, etCountry, etPostalCode;
    private Button btnCancel, btnAddAddress;

    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;
    private int contactId;
    private int addressId;

    private String originalStreet, originalCity, originalProvince, originalCountry, originalPostalCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPrefManager = SharedPrefManager.getInstance(this);

        // Get data from intent
        contactId = getIntent().getIntExtra("contact_id", -1);
        addressId = getIntent().getIntExtra("address_id", -1);
        originalStreet = getIntent().getStringExtra("street");
        originalCity = getIntent().getStringExtra("city");
        originalProvince = getIntent().getStringExtra("province");
        originalCountry = getIntent().getStringExtra("country");
        originalPostalCode = getIntent().getStringExtra("postal_code");

        if (contactId == -1 || addressId == -1) {
            Toast.makeText(this, "Invalid address", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        populateFields();
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
        btnAddAddress = findViewById(R.id.btnAddAddress);
    }

    private void populateFields() {
        etStreet.setText(originalStreet);
        etCity.setText(originalCity);
        etProvince.setText(originalProvince);
        etCountry.setText(originalCountry);
        etPostalCode.setText(originalPostalCode);
    }

    private void setupListeners() {
        llBackToContactDetails.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnAddAddress.setOnClickListener(v -> updateAddress());
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
                        Toast.makeText(EditAddressActivity.this,
                                "Failed to load contact info", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayContactInfo(Contact contact) {
        String fullName = contact.firstName + " " + contact.lastName;
        tvContactName.setText(fullName);
        tvContactEmail.setText(contact.email);
    }

    private void updateAddress() {
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

        DialogHelper.showLoadingDialog(this, "Updating address...");
        btnAddAddress.setEnabled(false);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.street = street;
        request.city = city;
        request.province = province;
        request.country = country;
        request.postalCode = postalCode;

        String token = sharedPrefManager.getToken();

        apiService.updateAddress(token, String.valueOf(contactId), String.valueOf(addressId), request)
                .enqueue(new Callback<ApiResponse<Address>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Address>> call, Response<ApiResponse<Address>> response) {
                        DialogHelper.dismissLoadingDialog();
                        btnAddAddress.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            DialogHelper.showSuccessDialog(
                                    EditAddressActivity.this,
                                    "Address updated successfully!",
                                    () -> finish()
                            );
                        } else {
                            String errorMessage = "Failed to update address";
                            if (response.body() != null && response.body().errors != null) {
                                errorMessage = response.body().errors;
                            }
                            DialogHelper.showErrorDialog(EditAddressActivity.this, errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Address>> call, Throwable t) {
                        DialogHelper.dismissLoadingDialog();
                        btnAddAddress.setEnabled(true);
                        DialogHelper.showFailureDialog(EditAddressActivity.this, t);
                    }
                });
    }
}