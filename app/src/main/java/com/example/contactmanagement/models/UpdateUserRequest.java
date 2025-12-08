package com.example.contactmanagement.models;

import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {
    @SerializedName("name")
    public String name;

    @SerializedName("password")
    public String password;
}