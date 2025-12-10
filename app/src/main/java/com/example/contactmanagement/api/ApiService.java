package com.example.contactmanagement.api;

import com.example.contactmanagement.models.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // Auth endpoints
    @POST("users")
    Call<ApiResponse<User>> register(@Body RegisterRequest request);

    @POST("users/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @GET("users/current")
    Call<ApiResponse<User>> getCurrentUser(@Header("Authorization") String token);

    @PATCH("users/current")
    Call<ApiResponse<User>> updateUser(@Header("Authorization") String token, @Body UpdateUserRequest request);

    @DELETE("users/logout")
    Call<ApiResponse<String>> logout(@Header("Authorization") String token);

    // Contact endpoints
    @POST("contacts")
    Call<ApiResponse<Contact>> createContact(@Header("Authorization") String token, @Body CreateContactRequest request);

    @GET("contacts")
    Call<ContactsResponse> getContacts(
            @Header("Authorization") String token,
            @Query("name") String name,
            @Query("email") String email,
            @Query("phone") String phone,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("contacts/{contactId}")
    Call<ApiResponse<Contact>> getContact(@Header("Authorization") String token, @Path("contactId") String contactId);

    @PUT("contacts/{contactId}")
    Call<ApiResponse<Contact>> updateContact(@Header("Authorization") String token, @Path("contactId") String contactId, @Body UpdateContactRequest request);

    @DELETE("contacts/{contactId}")
    Call<ApiResponse<String>> deleteContact(@Header("Authorization") String token, @Path("contactId") String contactId);

    // Address endpoints
    @POST("contacts/{contactId}/addresses")
    Call<ApiResponse<Address>> createAddress(@Header("Authorization") String token, @Path("contactId") String contactId, @Body CreateAddressRequest request);

    @GET("contacts/{contactId}/addresses")
    Call<ApiResponse<AddressListResponse>> getAddresses(@Header("Authorization") String token, @Path("contactId") String contactId);

    @GET("contacts/{contactId}/addresses/{addressId}")
    Call<ApiResponse<Address>> getAddress(@Header("Authorization") String token, @Path("contactId") String contactId, @Path("addressId") String addressId);

    @PUT("contacts/{contactId}/addresses/{addressId}")
    Call<ApiResponse<Address>> updateAddress(@Header("Authorization") String token, @Path("contactId") String contactId, @Path("addressId") String addressId, @Body UpdateAddressRequest request);

    @DELETE("contacts/{contactId}/addresses/{addressId}")
    Call<ApiResponse<String>> deleteAddress(@Header("Authorization") String token, @Path("contactId") String contactId, @Path("addressId") String addressId);
}