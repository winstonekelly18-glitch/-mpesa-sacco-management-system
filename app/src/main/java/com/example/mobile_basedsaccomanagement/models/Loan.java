package com.example.mobile_basedsaccomanagement.models;

public class Loan {
    private long id;
    private String loanNumber;
    private long userId;
    private String loanType;
    private double requestedAmount;
    private double interestRate;
    private double totalRepayment;
    private double monthlyInstallment;
    private int durationMonths;
    private String purpose;
    private String repaymentFrequency;
    private double amountRepaid;
    private double outstandingBalance;
    private String status; // "PENDING", "APPROVED", "REJECTED", "ACTIVE", "PAID"
    private String issuedDate;
    private String nextRepaymentDate;

    public Loan() {}

    public Loan(long id, String loanNumber, long userId, String loanType, double requestedAmount,
                double interestRate, double totalRepayment, double monthlyInstallment,
                int durationMonths, String purpose, String repaymentFrequency, double amountRepaid,
                double outstandingBalance, String status, String issuedDate, String nextRepaymentDate) {
        this.id = id;
        this.loanNumber = loanNumber;
        this.userId = userId;
        this.loanType = loanType;
        this.requestedAmount = requestedAmount;
        this.interestRate = interestRate;
        this.totalRepayment = totalRepayment;
        this.monthlyInstallment = monthlyInstallment;
        this.durationMonths = durationMonths;
        this.purpose = purpose;
        this.repaymentFrequency = repaymentFrequency;
        this.amountRepaid = amountRepaid;
        this.outstandingBalance = outstandingBalance;
        this.status = status;
        this.issuedDate = issuedDate;
        this.nextRepaymentDate = nextRepaymentDate;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getLoanNumber() { return loanNumber; }
    public void setLoanNumber(String loanNumber) { this.loanNumber = loanNumber; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public double getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(double requestedAmount) { this.requestedAmount = requestedAmount; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public double getTotalRepayment() { return totalRepayment; }
    public void setTotalRepayment(double totalRepayment) { this.totalRepayment = totalRepayment; }

    public double getMonthlyInstallment() { return monthlyInstallment; }
    public void setMonthlyInstallment(double monthlyInstallment) { this.monthlyInstallment = monthlyInstallment; }

    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getRepaymentFrequency() { return repaymentFrequency; }
    public void setRepaymentFrequency(String repaymentFrequency) { this.repaymentFrequency = repaymentFrequency; }

    public double getAmountRepaid() { return amountRepaid; }
    public void setAmountRepaid(double amountRepaid) { this.amountRepaid = amountRepaid; }

    public double getOutstandingBalance() { return outstandingBalance; }
    public void setOutstandingBalance(double outstandingBalance) { this.outstandingBalance = outstandingBalance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIssuedDate() { return issuedDate; }
    public void setIssuedDate(String issuedDate) { this.issuedDate = issuedDate; }

    public String getNextRepaymentDate() { return nextRepaymentDate; }
    public void setNextRepaymentDate(String nextRepaymentDate) { this.nextRepaymentDate = nextRepaymentDate; }
}
