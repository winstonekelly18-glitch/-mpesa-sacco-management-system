package com.example.mobile_basedsaccomanagement.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "loans",
    foreignKeys = {
        @ForeignKey(
            entity = MemberEntity.class,
            parentColumns = "id",
            childColumns = "member_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = SaccoProductEntity.class,
            parentColumns = "id",
            childColumns = "product_id",
            onDelete = ForeignKey.RESTRICT
        )
    },
    indices = {
        @Index(value = {"member_id"}),
        @Index(value = {"product_id"}),
        @Index(value = {"loan_number"}, unique = true)
    }
)
public class LoanEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "member_id")
    private long memberId;

    @ColumnInfo(name = "product_id")
    private long productId;

    @ColumnInfo(name = "loan_number")
    private String loanNumber;

    @ColumnInfo(name = "requested_amount")
    private double requestedAmount;

    @ColumnInfo(name = "interest_rate")
    private double interestRate;

    @ColumnInfo(name = "total_repayment")
    private double totalRepayment;

    @ColumnInfo(name = "monthly_installment")
    private double monthlyInstallment;

    @ColumnInfo(name = "duration_months")
    private int durationMonths;

    @ColumnInfo(name = "purpose")
    private String purpose;

    @ColumnInfo(name = "repayment_frequency")
    private String repaymentFrequency;

    @ColumnInfo(name = "amount_repaid")
    private double amountRepaid;

    @ColumnInfo(name = "outstanding_balance")
    private double outstandingBalance;

    @ColumnInfo(name = "status")
    private String status; // "PENDING", "APPROVED", "REJECTED", "ACTIVE", "FULLY_PAID"

    @ColumnInfo(name = "applied_date")
    private String appliedDate;

    @ColumnInfo(name = "approved_date")
    private String approvedDate;

    public LoanEntity() {}

    public LoanEntity(long memberId, long productId, String loanNumber, double requestedAmount,
                      double interestRate, double totalRepayment, double monthlyInstallment,
                      int durationMonths, String purpose, String repaymentFrequency, double amountRepaid,
                      double outstandingBalance, String status, String appliedDate, String approvedDate) {
        this.memberId = memberId;
        this.productId = productId;
        this.loanNumber = loanNumber;
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
        this.appliedDate = appliedDate;
        this.approvedDate = approvedDate;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getMemberId() { return memberId; }
    public void setMemberId(long memberId) { this.memberId = memberId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public String getLoanNumber() { return loanNumber; }
    public void setLoanNumber(String loanNumber) { this.loanNumber = loanNumber; }

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

    public String getAppliedDate() { return appliedDate; }
    public void setAppliedDate(String appliedDate) { this.appliedDate = appliedDate; }

    public String getApprovedDate() { return approvedDate; }
    public void setApprovedDate(String approvedDate) { this.approvedDate = approvedDate; }
}
