package com.example.mobile_basedsaccomanagement.models;

public class Transaction {
    private long id;
    private String transactionId;
    private long userId;
    private String type; // "DEPOSIT", "WITHDRAWAL", "LOAN_DISBURSEMENT", "LOAN_REPAYMENT", "SHARE_CONTRIBUTION"
    private double amount;
    private String date;
    private String time;
    private String description;
    private String status; // "COMPLETED", "FAILED", "PENDING"

    public Transaction() {}

    public Transaction(long id, String transactionId, long userId, String type, double amount,
                       String date, String time, String description, String status) {
        this.id = id;
        this.transactionId = transactionId;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.description = description;
        this.status = status;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
