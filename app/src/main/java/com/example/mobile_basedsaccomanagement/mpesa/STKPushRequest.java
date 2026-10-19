package com.example.mobile_basedsaccomanagement.mpesa;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class STKPushRequest {
    @SerializedName("BusinessShortCode")
    @Expose
    private String businessShortCode;

    @SerializedName("Password")
    @Expose
    private String password;

    @SerializedName("Timestamp")
    @Expose
    private String timestamp;

    @SerializedName("TransactionType")
    @Expose
    private String transactionType;

    @SerializedName("Amount")
    @Expose
    private String amount;

    @SerializedName("PartyA")
    @Expose
    private String partyA;

    @SerializedName("PartyB")
    @Expose
    private String partyB;

    @SerializedName("PhoneNumber")
    @Expose
    private String phoneNumber;

    @SerializedName("CallBackURL")
    @Expose
    private String callBackURL;

    @SerializedName("AccountReference")
    @Expose
    private String accountReference;

    @SerializedName("TransactionDesc")
    @Expose
    private String transactionDesc;

    public STKPushRequest(String businessShortCode, String password, String timestamp, String transactionType,
                          String amount, String partyA, String partyB, String phoneNumber,
                          String callBackURL, String accountReference, String transactionDesc) {
        this.businessShortCode = businessShortCode;
        this.password = password;
        this.timestamp = timestamp;
        this.transactionType = transactionType;
        this.amount = amount;
        this.partyA = partyA;
        this.partyB = partyB;
        this.phoneNumber = phoneNumber;
        this.callBackURL = callBackURL;
        this.accountReference = accountReference;
        this.transactionDesc = transactionDesc;
    }
}
