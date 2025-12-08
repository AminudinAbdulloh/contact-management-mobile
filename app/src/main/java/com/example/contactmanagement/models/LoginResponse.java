package com.example.contactmanagement.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("username")
    public String username;

    @SerializedName("name")
    public String name;

    @SerializedName("token")
    public String token;
}