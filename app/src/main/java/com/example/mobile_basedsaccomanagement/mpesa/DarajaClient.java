package com.example.mobile_basedsaccomanagement.mpesa;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DarajaClient {
    private static DarajaApi api;

    public static DarajaApi get() {
        if (api == null) {
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(log).build();

            api = new Retrofit.Builder()
                    .baseUrl("https://sandbox.safaricom.co.ke/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(DarajaApi.class);
        }
        return api;
    }
}
