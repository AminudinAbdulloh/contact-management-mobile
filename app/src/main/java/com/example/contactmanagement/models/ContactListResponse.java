package com.example.contactmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ContactListResponse {
    @SerializedName("data")
    public List<Contact> data;
}