package com.example.mobile_basedsaccomanagement.mpesa;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DarajaApiClient {

    @GET("oauth/v1/generate?grant_type=client_credentials")
    Call<AccessToken> getAccessToken(@Header("Authorization") String authHeader);

    @POST("mpesa/stkpush/v1/processrequest")
    Call<STKPushResponse> sendStkPush(
            @Header("Authorization") String authHeader,
            @Body STKPushRequest stkPushRequest
    );
}
