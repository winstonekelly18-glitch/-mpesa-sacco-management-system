package com.example.mobile_basedsaccomanagement.mpesa;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DarajaClient {
    private static DarajaApi api;
    public static boolean USE_PRODUCTION = false;

    public static String getBaseUrl() {
        return USE_PRODUCTION ? "https://api.safaricom.co.ke/" : "https://sandbox.safaricom.co.ke/";
    }

    public static DarajaApi get() {
        if (api == null) {
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(log).build();

            api = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(DarajaApi.class);
        }
        return api;
    }
}
