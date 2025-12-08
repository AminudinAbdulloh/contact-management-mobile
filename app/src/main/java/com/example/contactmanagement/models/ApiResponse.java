package com.example.contactmanagement.models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("data")
    public T data;

    @SerializedName("errors")
    public String errors;

    @SerializedName("paging")
    public Paging paging;
}