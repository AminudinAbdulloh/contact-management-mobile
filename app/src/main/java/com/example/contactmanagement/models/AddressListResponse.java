package com.example.contactmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AddressListResponse {
    @SerializedName("data")
    public List<Address> data;

    @SerializedName("paging")
    public Paging paging;
}