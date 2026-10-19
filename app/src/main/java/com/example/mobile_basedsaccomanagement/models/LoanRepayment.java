package com.example.mobile_basedsaccomanagement.models;

public class LoanRepayment {
    private long id;
    private long loanId;
    private long userId;
    private double amountPaid;
    private String paymentDate;
    private String paymentMethod;
    private String referenceCode;

    public LoanRepayment() {}

    public LoanRepayment(long id, long loanId, long userId, double amountPaid, String paymentDate, String paymentMethod, String referenceCode) {
        this.id = id;
        this.loanId = loanId;
        this.userId = userId;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.referenceCode = referenceCode;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getLoanId() { return loanId; }
    public void setLoanId(long loanId) { this.loanId = loanId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }
}
