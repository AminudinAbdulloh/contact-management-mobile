package com.example.contactmanagement.models;

import com.google.gson.annotations.SerializedName;

public class Paging {
    @SerializedName("page")
    public int page;

    @SerializedName("total_page")
    public int totalPage;

    @SerializedName("size")
    public int size;

    @SerializedName("total_item")
    public int totalItem;
}