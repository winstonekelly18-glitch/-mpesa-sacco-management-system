package com.example.mobile_basedsaccomanagement.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "transactions",
    foreignKeys = @ForeignKey(
        entity = MemberEntity.class,
        parentColumns = "id",
        childColumns = "member_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {
        @Index(value = {"member_id"}),
        @Index(value = {"transaction_code"}, unique = true)
    }
)
public class TransactionEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "transaction_code")
    private String transactionCode;

    @ColumnInfo(name = "member_id")
    private long memberId;

    @ColumnInfo(name = "type")
    private String type; // "DEPOSIT", "WITHDRAWAL", "LOAN_DISBURSEMENT", "LOAN_REPAYMENT", "SHARE_CONTRIBUTION", "TRANSFER"

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "time")
    private String time;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "status")
    private String status;

    public TransactionEntity() {}

    public TransactionEntity(String transactionCode, long memberId, String type, double amount,
                             String date, String time, String description, String status) {
        this.transactionCode = transactionCode;
        this.memberId = memberId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.description = description;
        this.status = status;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }

    public long getMemberId() { return memberId; }
    public void setMemberId(long memberId) { this.memberId = memberId; }

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
