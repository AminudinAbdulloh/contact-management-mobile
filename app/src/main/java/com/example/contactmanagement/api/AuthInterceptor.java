package com.example.contactmanagement.api;

import android.content.Context;
import android.content.Intent;

import com.example.contactmanagement.LoginActivity;
import com.example.contactmanagement.utils.SharedPrefManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        // Jika response 401 Unauthorized, berarti token sudah kadaluarsa
        if (response.code() == 401) {
            // Hapus session
            SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
            sharedPrefManager.clearSession();

            // Redirect ke login activity
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }

        return response;
    }
}