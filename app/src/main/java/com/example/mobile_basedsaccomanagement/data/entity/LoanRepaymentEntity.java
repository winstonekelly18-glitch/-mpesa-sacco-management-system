package com.example.mobile_basedsaccomanagement.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "loan_repayments",
    foreignKeys = {
        @ForeignKey(
            entity = LoanEntity.class,
            parentColumns = "id",
            childColumns = "loan_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = MemberEntity.class,
            parentColumns = "id",
            childColumns = "member_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = {"loan_id"}),
        @Index(value = {"member_id"})
    }
)
public class LoanRepaymentEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "loan_id")
    private long loanId;

    @ColumnInfo(name = "member_id")
    private long memberId;

    @ColumnInfo(name = "amount_paid")
    private double amountPaid;

    @ColumnInfo(name = "payment_date")
    private String paymentDate;

    @ColumnInfo(name = "payment_method")
    private String paymentMethod;

    @ColumnInfo(name = "reference_code")
    private String referenceCode;

    public LoanRepaymentEntity() {}

    public LoanRepaymentEntity(long loanId, long memberId, double amountPaid, String paymentDate,
                               String paymentMethod, String referenceCode) {
        this.loanId = loanId;
        this.memberId = memberId;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.referenceCode = referenceCode;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getLoanId() { return loanId; }
    public void setLoanId(long loanId) { this.loanId = loanId; }

    public long getMemberId() { return memberId; }
    public void setMemberId(long memberId) { this.memberId = memberId; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }
}
