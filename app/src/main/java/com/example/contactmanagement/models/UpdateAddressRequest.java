package com.example.contactmanagement.models;

import com.google.gson.annotations.SerializedName;

public class UpdateAddressRequest {
    @SerializedName("street")
    public String street;

    @SerializedName("city")
    public String city;

    @SerializedName("province")
    public String province;

    @SerializedName("country")
    public String country;

    @SerializedName("postal_code")
    public String postalCode;
}