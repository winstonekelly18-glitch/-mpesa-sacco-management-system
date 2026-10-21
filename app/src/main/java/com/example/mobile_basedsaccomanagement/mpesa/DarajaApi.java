package com.example.mobile_basedsaccomanagement.mpesa;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DarajaApi {

    @GET("oauth/v1/generate?grant_type=client_credentials")
    Call<TokenResponse> getToken(@Header("Authorization") String basicAuth);

    @POST("mpesa/stkpush/v1/processrequest")
    Call<StkResponse> stkPush(@Header("Authorization") String bearer, @Body StkRequest body);

    @POST("mpesa/stkpushquery/v1/query")
    Call<StkQueryResponse> stkQuery(@Header("Authorization") String bearer, @Body StkQueryRequest body);
}
