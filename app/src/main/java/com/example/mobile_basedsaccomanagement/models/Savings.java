package com.example.mobile_basedsaccomanagement.models;

public class Savings {
    private long id;
    private long userId;
    private double balance;
    private double totalContributions;
    private double monthlyContribution;

    public Savings() {}

    public Savings(long id, long userId, double balance, double totalContributions, double monthlyContribution) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.totalContributions = totalContributions;
        this.monthlyContribution = monthlyContribution;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public double getTotalContributions() { return totalContributions; }
    public void setTotalContributions(double totalContributions) { this.totalContributions = totalContributions; }

    public double getMonthlyContribution() { return monthlyContribution; }
    public void setMonthlyContribution(double monthlyContribution) { this.monthlyContribution = monthlyContribution; }
}
