package com.example.mobile_basedsaccomanagement.mpesa;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class STKPushResponse {
    @SerializedName("MerchantRequestID")
    @Expose
    private String merchantRequestID;

    @SerializedName("CheckoutRequestID")
    @Expose
    private String checkoutRequestID;

    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;

    @SerializedName("ResponseDescription")
    @Expose
    private String responseDescription;

    @SerializedName("CustomerMessage")
    @Expose
    private String customerMessage;

    public String getMerchantRequestID() { return merchantRequestID; }
    public String getCheckoutRequestID() { return checkoutRequestID; }
    public String getResponseCode() { return responseCode; }
    public String getResponseDescription() { return responseDescription; }
    public String getCustomerMessage() { return customerMessage; }
}
