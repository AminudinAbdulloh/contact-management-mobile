package com.example.contactmanagement.models;

import com.google.gson.annotations.SerializedName;

public class CreateContactRequest {
    @SerializedName("first_name")
    public String firstName;

    @SerializedName("last_name")
    public String lastName;

    @SerializedName("email")
    public String email;

    @SerializedName("phone")
    public String phone;
}