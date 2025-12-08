package com.example.contactmanagement;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactmanagement.adapters.ContactAdapter;
import com.example.contactmanagement.api.ApiClient;
import com.example.contactmanagement.api.ApiService;
import com.example.contactmanagement.models.ApiResponse;
import com.example.contactmanagement.models.Contact;
import com.example.contactmanagement.models.ContactListResponse;
import com.example.contactmanagement.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsActivity extends AppCompatActivity implements ContactAdapter.OnContactClickListener {

    private LinearLayout llProfile, llLogout, llSearchHeader, llSearchFields;
    private CardView cardCreateContact;
    private ImageView ivSearchToggle;
    private EditText etSearchName, etSearchEmail, etSearchPhone;
    private Button btnSearch;
    private RecyclerView rvContacts;
    private TextView tvPageNumber;

    private ContactAdapter adapter;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;

    private int currentPage = 1;
    private int totalPage = 1;
    private boolean isSearchExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPrefManager = SharedPrefManager.getInstance(this);

        // Check if logged in
        if (!sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupListeners();
        loadContacts(currentPage, null, null, null);
    }

    private void initViews() {
        llProfile = findViewById(R.id.llProfile);
        llLogout = findViewById(R.id.llLogout);
        llSearchHeader = findViewById(R.id.llSearchHeader);
        llSearchFields = findViewById(R.id.llSearchFields);
        ivSearchToggle = findViewById(R.id.ivSearchToggle);
        cardCreateContact = findViewById(R.id.cardCreateContact);
        etSearchName = findViewById(R.id.etSearchName);
        etSearchEmail = findViewById(R.id.etSearchEmail);
        etSearchPhone = findViewById(R.id.etSearchPhone);
        btnSearch = findViewById(R.id.btnSearch);
        rvContacts = findViewById(R.id.rvContacts);
        tvPageNumber = findViewById(R.id.tvPageNumber);
    }

    private void setupRecyclerView() {
        adapter = new ContactAdapter(this);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(adapter);
    }

    private void setupListeners() {
        llProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        llLogout.setOnClickListener(v -> showLogoutDialog());

        llSearchHeader.setOnClickListener(v -> toggleSearch());

        btnSearch.setOnClickListener(v -> {
            String name = etSearchName.getText().toString().trim();
            String email = etSearchEmail.getText().toString().trim();
            String phone = etSearchPhone.getText().toString().trim();
            currentPage = 1;
            loadContacts(currentPage,
                    name.isEmpty() ? null : name,
                    email.isEmpty() ? null : email,
                    phone.isEmpty() ? null : phone);
        });

        cardCreateContact.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateContactActivity.class));
        });

        tvPageNumber.setOnClickListener(v -> showPageDialog());
    }

    private void toggleSearch() {
        isSearchExpanded = !isSearchExpanded;
        llSearchFields.setVisibility(isSearchExpanded ? View.VISIBLE : View.GONE);
        ivSearchToggle.setRotation(isSearchExpanded ? 0 : 180);
    }

    private void loadContacts(int page, String name, String email, String phone) {
        String token = sharedPrefManager.getToken();

        apiService.getContacts(token, name, email, phone, page, 10)
                .enqueue(new Callback<ApiResponse<ContactListResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ContactListResponse>> call, Response<ApiResponse<ContactListResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ContactListResponse data = response.body().data;
                            if (data != null && data.data != null) {
                                adapter.setContacts(data.data);

                                if (response.body().paging != null) {
                                    currentPage = response.body().paging.page;
                                    totalPage = response.body().paging.totalPage;
                                    tvPageNumber.setText(String.valueOf(currentPage));
                                }
                            }
                        } else {
                            Toast.makeText(ContactsActivity.this, "Failed to load contacts", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ContactListResponse>> call, Throwable t) {
                        Toast.makeText(ContactsActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showPageDialog() {
        if (totalPage <= 1) return;

        String[] pages = new String[totalPage];
        for (int i = 0; i < totalPage; i++) {
            pages[i] = "Page " + (i + 1);
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Page")
                .setItems(pages, (dialog, which) -> {
                    currentPage = which + 1;
                    loadContacts(currentPage, null, null, null);
                })
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        String token = sharedPrefManager.getToken();

        apiService.logout(token).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                sharedPrefManager.clearSession();
                startActivity(new Intent(ContactsActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                // Logout locally even if API call fails
                sharedPrefManager.clearSession();
                startActivity(new Intent(ContactsActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onEditClick(Contact contact) {
        Intent intent = new Intent(this, EditContactActivity.class);
        intent.putExtra("contact_id", contact.id);
        intent.putExtra("first_name", contact.firstName);
        intent.putExtra("last_name", contact.lastName);
        intent.putExtra("email", contact.email);
        intent.putExtra("phone", contact.phone);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Contact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete " + contact.firstName + " " + contact.lastName + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteContact(contact.id))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onContactClick(Contact contact) {
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra("contact_id", contact.id);
        startActivity(intent);
    }

    private void deleteContact(String contactId) {
        String token = sharedPrefManager.getToken();

        apiService.deleteContact(token, contactId).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ContactsActivity.this, "Contact deleted", Toast.LENGTH_SHORT).show();
                    loadContacts(currentPage, null, null, null);
                } else {
                    Toast.makeText(ContactsActivity.this, "Failed to delete contact", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(ContactsActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts(currentPage, null, null, null);
    }
}