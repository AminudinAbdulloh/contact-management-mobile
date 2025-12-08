package com.example.contactmanagement.models;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;
}