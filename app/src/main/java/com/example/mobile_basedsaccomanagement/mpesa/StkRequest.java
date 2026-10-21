package com.example.mobile_basedsaccomanagement.mpesa;

public class StkRequest {
    public String BusinessShortCode;
    public String Password;
    public String Timestamp;
    public String TransactionType = "CustomerPayBillOnline";
    public String Amount;
    public String PartyA;
    public String PartyB;
    public String PhoneNumber;
    public String CallBackURL;
    public String AccountReference;
    public String TransactionDesc;
}
