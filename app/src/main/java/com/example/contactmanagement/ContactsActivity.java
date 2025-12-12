package com.example.contactmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactmanagement.adapters.ContactAdapter;
import com.example.contactmanagement.models.ApiResponse;
import com.example.contactmanagement.models.Contact;
import com.example.contactmanagement.models.ContactsResponse;
import com.example.contactmanagement.utils.DialogHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsActivity extends BaseActivity implements ContactAdapter.OnContactClickListener {

    private LinearLayout llSearchHeader, llSearchFields, llPaginationContainer;
    private CardView cardCreateContact;
    private ImageView ivSearchToggle;
    private EditText etSearchName, etSearchEmail, etSearchPhone;
    private Button btnSearch;
    private RecyclerView rvContacts;
    private ContactAdapter adapter;

    private int currentPage = 1;
    private int totalPage = 1;
    private boolean isSearchExpanded = false;

    private String currentSearchName = null;
    private String currentSearchEmail = null;
    private String currentSearchPhone = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        checkAuthentication();

        initViews();
        setupRecyclerView();
        setupListeners();
        loadContacts(currentPage, null, null, null, false);
    }

    private void initViews() {
        llSearchHeader = findViewById(R.id.llSearchHeader);
        llSearchFields = findViewById(R.id.llSearchFields);
        ivSearchToggle = findViewById(R.id.ivSearchToggle);
        cardCreateContact = findViewById(R.id.cardCreateContact);
        etSearchName = findViewById(R.id.etSearchName);
        etSearchEmail = findViewById(R.id.etSearchEmail);
        etSearchPhone = findViewById(R.id.etSearchPhone);
        btnSearch = findViewById(R.id.btnSearch);
        rvContacts = findViewById(R.id.rvContacts);
        llPaginationContainer = findViewById(R.id.llPaginationContainer);
    }

    private void setupRecyclerView() {
        adapter = new ContactAdapter(this);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(adapter);
    }

    private void setupListeners() {
        llSearchHeader.setOnClickListener(v -> toggleSearch());

        btnSearch.setOnClickListener(v -> performSearch());

        cardCreateContact.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateContactActivity.class));
        });
    }

    private void toggleSearch() {
        isSearchExpanded = !isSearchExpanded;
        llSearchFields.setVisibility(isSearchExpanded ? View.VISIBLE : View.GONE);
        ivSearchToggle.setRotation(isSearchExpanded ? 0 : 180);
    }

    private void performSearch() {
        String name = etSearchName.getText().toString().trim();
        String email = etSearchEmail.getText().toString().trim();
        String phone = etSearchPhone.getText().toString().trim();

        // Update current search parameters
        currentSearchName = name.isEmpty() ? null : name;
        currentSearchEmail = email.isEmpty() ? null : email;
        currentSearchPhone = phone.isEmpty() ? null : phone;

        // Reset to page 1 when searching
        currentPage = 1;

        // Load contacts with search parameters
        loadContacts(currentPage, currentSearchName, currentSearchEmail, currentSearchPhone, true);
    }

    private void loadContacts(int page, String name, String email, String phone, boolean showLoading) {
        if (showLoading) {
            DialogHelper.showLoadingDialog(ContactsActivity.this, "Loading contacts...");
        }

        String token = sharedPrefManager.getToken();

        apiService.getContacts(token, name, email, phone, page, 10)
                .enqueue(new Callback<ContactsResponse>() {
                    @Override
                    public void onResponse(Call<ContactsResponse> call, Response<ContactsResponse> response) {
                        if (showLoading) {
                            DialogHelper.dismissLoadingDialog();
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            ContactsResponse data = response.body();
                            if (data.data != null) {
                                adapter.setContacts(data.data);

                                if (data.paging != null) {
                                    currentPage = data.paging.page;
                                    totalPage = data.paging.totalPage;
                                    buildPaginationButtons();
                                }
                            }
                        } else {
                            Toast.makeText(ContactsActivity.this, "Failed to load contacts", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ContactsResponse> call, Throwable t) {
                        if (showLoading) {
                            DialogHelper.dismissLoadingDialog();
                        }

                        Toast.makeText(ContactsActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void buildPaginationButtons() {
        llPaginationContainer.removeAllViews();

        if (totalPage <= 1) {
            llPaginationContainer.setVisibility(View.GONE);
            return;
        }

        llPaginationContainer.setVisibility(View.VISIBLE);

        int marginPx = (int) (4 * getResources().getDisplayMetrics().density);

        // Add Previous button
        TextView prevBtn = createPrevButton();
        LinearLayout.LayoutParams prevParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (int) (44 * getResources().getDisplayMetrics().density)
        );
        prevParams.setMargins(marginPx, 0, marginPx, 0);
        prevBtn.setLayoutParams(prevParams);
        llPaginationContainer.addView(prevBtn);

        // Add page number buttons
        for (int i = 1; i <= totalPage; i++) {
            TextView pageBtn = createPageButton(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (44 * getResources().getDisplayMetrics().density),
                    (int) (44 * getResources().getDisplayMetrics().density)
            );
            params.setMargins(marginPx, 0, marginPx, 0);
            pageBtn.setLayoutParams(params);
            llPaginationContainer.addView(pageBtn);
        }

        // Add Next button
        TextView nextBtn = createNextButton();
        LinearLayout.LayoutParams nextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (int) (44 * getResources().getDisplayMetrics().density)
        );
        nextParams.setMargins(marginPx, 0, marginPx, 0);
        nextBtn.setLayoutParams(nextParams);
        llPaginationContainer.addView(nextBtn);
    }

    private TextView createPrevButton() {
        TextView btn = new TextView(this);
        btn.setText("Prev");
        btn.setGravity(android.view.Gravity.CENTER);
        btn.setTextSize(14);
        btn.setTextColor(getResources().getColor(R.color.white, null));
        btn.setBackgroundResource(R.drawable.bg_page_button);
        btn.setClickable(true);
        btn.setFocusable(true);
        btn.setPadding(
                (int) (12 * getResources().getDisplayMetrics().density),
                0,
                (int) (16 * getResources().getDisplayMetrics().density),
                0
        );

        btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chevron_left, 0, 0, 0);
        btn.setCompoundDrawablePadding((int) (8 * getResources().getDisplayMetrics().density));

        btn.setEnabled(currentPage > 1);
        btn.setAlpha(currentPage > 1 ? 1.0f : 0.5f);

        btn.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadContacts(currentPage, currentSearchName, currentSearchEmail, currentSearchPhone, true);
            }
        });

        return btn;
    }

    private TextView createPageButton(int pageNum) {
        TextView btn = new TextView(this);
        btn.setText(String.valueOf(pageNum));
        btn.setGravity(android.view.Gravity.CENTER);
        btn.setTextSize(14);
        btn.setTextColor(getResources().getColor(R.color.white, null));
        btn.setBackgroundResource(R.drawable.bg_page_button_selector);
        btn.setSelected(pageNum == currentPage);
        btn.setClickable(true);
        btn.setFocusable(true);

        btn.setOnClickListener(v -> {
            if (pageNum != currentPage) {
                currentPage = pageNum;
                loadContacts(currentPage, currentSearchName, currentSearchEmail, currentSearchPhone, true);
            }
        });

        return btn;
    }

    private TextView createNextButton() {
        TextView btn = new TextView(this);
        btn.setText("Next");
        btn.setGravity(android.view.Gravity.CENTER);
        btn.setTextSize(14);
        btn.setTextColor(getResources().getColor(R.color.white, null));
        btn.setBackgroundResource(R.drawable.bg_page_button);
        btn.setClickable(true);
        btn.setFocusable(true);
        btn.setPadding(
                (int) (16 * getResources().getDisplayMetrics().density),
                0,
                (int) (12 * getResources().getDisplayMetrics().density),
                0
        );

        btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_right, 0);
        btn.setCompoundDrawablePadding((int) (8 * getResources().getDisplayMetrics().density));

        btn.setEnabled(currentPage < totalPage);
        btn.setAlpha(currentPage < totalPage ? 1.0f : 0.5f);

        btn.setOnClickListener(v -> {
            if (currentPage < totalPage) {
                currentPage++;
                loadContacts(currentPage, currentSearchName, currentSearchEmail, currentSearchPhone, true);
            }
        });

        return btn;
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
        DialogHelper.showConfirmationDialog(
                this,
                "Delete Contact",
                "Are you sure you want to delete " + contact.firstName + " " + contact.lastName + "?",
                new DialogHelper.OnDialogActionListener() {
                    @Override
                    public void onPositiveClick() {
                        deleteContact(contact.id);
                    }

                    @Override
                    public void onNegativeClick() {
                        // Do nothing, dialog will dismiss
                    }
                }
        );
    }

    @Override
    public void onContactClick(Contact contact) {
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra("contact_id", contact.id);
        startActivity(intent);
    }

    private void deleteContact(int contactId) {
        DialogHelper.showLoadingDialog(ContactsActivity.this, "Deleting contact...");
        String token = sharedPrefManager.getToken();

        apiService.deleteContact(token, String.valueOf(contactId)).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                DialogHelper.dismissLoadingDialog();
                if (response.isSuccessful()) {
                    DialogHelper.showSuccessDialog(
                            ContactsActivity.this,
                            "Contact deleted successfully",
                            () -> loadContacts(currentPage, currentSearchName, currentSearchEmail, currentSearchPhone, false)
                    );
                } else {
                    DialogHelper.showErrorDialog(ContactsActivity.this, "Failed to delete contact");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                DialogHelper.dismissLoadingDialog();
                DialogHelper.showFailureDialog(ContactsActivity.this, t);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts(currentPage, currentSearchName, currentSearchEmail, currentSearchPhone, false);
    }
}