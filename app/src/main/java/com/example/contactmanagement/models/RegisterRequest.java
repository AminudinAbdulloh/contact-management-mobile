package com.example.contactmanagement.models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;

    @SerializedName("name")
    public String name;
}