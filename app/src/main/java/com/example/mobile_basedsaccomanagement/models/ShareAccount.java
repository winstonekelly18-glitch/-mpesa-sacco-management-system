package com.example.mobile_basedsaccomanagement.models;

public class ShareAccount {
    private long id;
    private long userId;
    private int totalShares;
    private double shareValue;
    private double totalContribution;
    private int minRequiredShares;
    private String status; // "ACTIVE", "INCOMPLETE"

    public ShareAccount() {}

    public ShareAccount(long id, long userId, int totalShares, double shareValue, double totalContribution, int minRequiredShares, String status) {
        this.id = id;
        this.userId = userId;
        this.totalShares = totalShares;
        this.shareValue = shareValue;
        this.totalContribution = totalContribution;
        this.minRequiredShares = minRequiredShares;
        this.status = status;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public int getTotalShares() { return totalShares; }
    public void setTotalShares(int totalShares) { this.totalShares = totalShares; }

    public double getShareValue() { return shareValue; }
    public void setShareValue(double shareValue) { this.shareValue = shareValue; }

    public double getTotalContribution() { return totalContribution; }
    public void setTotalContribution(double totalContribution) { this.totalContribution = totalContribution; }

    public int getMinRequiredShares() { return minRequiredShares; }
    public void setMinRequiredShares(int minRequiredShares) { this.minRequiredShares = minRequiredShares; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
