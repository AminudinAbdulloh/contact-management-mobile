package com.example.contactmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ContactsResponse {
    @SerializedName("data")
    public List<Contact> data;

    @SerializedName("paging")
    public Paging paging;
}