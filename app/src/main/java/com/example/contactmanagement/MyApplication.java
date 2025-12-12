package com.example.contactmanagement;

import android.app.Application;

import com.example.contactmanagement.api.ApiClient;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize ApiClient dengan application context
        ApiClient.initialize(this);
    }
}