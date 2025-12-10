package com.example.contactmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactmanagement.adapters.AddressAdapter;
import com.example.contactmanagement.api.ApiClient;
import com.example.contactmanagement.api.ApiService;
import com.example.contactmanagement.models.Address;
import com.example.contactmanagement.models.AddressListResponse;
import com.example.contactmanagement.models.ApiResponse;
import com.example.contactmanagement.models.Contact;
import com.example.contactmanagement.utils.DialogHelper;
import com.example.contactmanagement.utils.SharedPrefManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactDetailActivity extends BaseActivity implements AddressAdapter.OnAddressClickListener {

    private LinearLayout llBackToContacts;
    private TextView tvContactName, tvFirstName, tvLastName, tvEmail, tvPhone;
    private CardView cardAddAddress;
    private RecyclerView rvAddresses;
    private Button btnCancel, btnSaveChanges;
    private AddressAdapter addressAdapter;
    private int contactId;
    private Contact currentContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        contactId = getIntent().getIntExtra("contact_id", -1);

        if (contactId == -1) {
            Toast.makeText(this, "Invalid contact", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupListeners();
        loadContactDetail();
        loadAddresses();
    }

    private void initViews() {
        llBackToContacts = findViewById(R.id.llBackToContacts);
        tvContactName = findViewById(R.id.tvContactName);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        cardAddAddress = findViewById(R.id.cardAddAddress);
        rvAddresses = findViewById(R.id.rvAddresses);
        btnCancel = findViewById(R.id.btnCancel);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
    }

    private void setupRecyclerView() {
        addressAdapter = new AddressAdapter(this);
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        rvAddresses.setAdapter(addressAdapter);
    }

    private void setupListeners() {
        llBackToContacts.setOnClickListener(v -> finish());

        btnCancel.setOnClickListener(v -> finish());

        btnSaveChanges.setOnClickListener(v -> {
            if (currentContact != null) {
                Intent intent = new Intent(this, EditContactActivity.class);
                intent.putExtra("contact_id", currentContact.id);
                intent.putExtra("first_name", currentContact.firstName);
                intent.putExtra("last_name", currentContact.lastName);
                intent.putExtra("email", currentContact.email);
                intent.putExtra("phone", currentContact.phone);
                startActivity(intent);
            }
        });

        cardAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddAddressActivity.class);
            intent.putExtra("contact_id", contactId);
            startActivity(intent);
        });
    }

    private void loadContactDetail() {
        String token = sharedPrefManager.getToken();

        apiService.getContact(token, String.valueOf(contactId))
                .enqueue(new Callback<ApiResponse<Contact>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Contact>> call, Response<ApiResponse<Contact>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentContact = response.body().data;
                            if (currentContact != null) {
                                displayContactInfo(currentContact);
                            }
                        } else {
                            Toast.makeText(ContactDetailActivity.this, "Failed to load contact details", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Contact>> call, Throwable t) {
                        Toast.makeText(ContactDetailActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayContactInfo(Contact contact) {
        String fullName = contact.firstName + " " + contact.lastName;
        tvContactName.setText(fullName);
        tvFirstName.setText(contact.firstName);
        tvLastName.setText(contact.lastName);
        tvEmail.setText(contact.email);
        tvPhone.setText(contact.phone);
    }

    private void loadAddresses() {
        String token = sharedPrefManager.getToken();

        apiService.getAddresses(token, String.valueOf(contactId))
                .enqueue(new Callback<AddressListResponse>() {
                    @Override
                    public void onResponse(Call<AddressListResponse> call, Response<AddressListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            AddressListResponse addressListResponse = response.body();

                            if (addressListResponse.data != null) {
                                addressAdapter.setAddresses(addressListResponse.data);
                            } else {
                                addressAdapter.setAddresses(new ArrayList<>());
                            }
                        } else {
                            Toast.makeText(ContactDetailActivity.this,
                                    "Failed to load addresses", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AddressListResponse> call, Throwable t) {
                        Toast.makeText(ContactDetailActivity.this,
                                "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onEditClick(Address address) {
        Intent intent = new Intent(this, EditAddressActivity.class);
        intent.putExtra("contact_id", contactId);
        intent.putExtra("address_id", address.id);
        intent.putExtra("street", address.street);
        intent.putExtra("city", address.city);
        intent.putExtra("province", address.province);
        intent.putExtra("country", address.country);
        intent.putExtra("postal_code", address.postalCode);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Address address) {
        DialogHelper.showConfirmationDialog(
                this,
                "Delete Address",
                "Are you sure you want to delete this address?",
                new DialogHelper.OnDialogActionListener() {
                    @Override
                    public void onPositiveClick() {
                        deleteAddress(address.id);
                    }

                    @Override
                    public void onNegativeClick() {
                        // Do nothing, dialog will dismiss
                    }
                }
        );
    }

    private void deleteAddress(int addressId) {
        DialogHelper.showLoadingDialog(this, "Deleting address...");
        String token = sharedPrefManager.getToken();

        apiService.deleteAddress(token, String.valueOf(contactId), String.valueOf(addressId))
                .enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                        DialogHelper.dismissLoadingDialog();
                        if (response.isSuccessful()) {
                            DialogHelper.showSuccessDialog(
                                    ContactDetailActivity.this,
                                    "Address deleted successfully",
                                    () -> loadAddresses()
                            );
                        } else {
                            DialogHelper.showErrorDialog(ContactDetailActivity.this, "Failed to delete address");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                        DialogHelper.dismissLoadingDialog();
                        DialogHelper.showFailureDialog(ContactDetailActivity.this, t);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContactDetail();
        loadAddresses();
    }
}