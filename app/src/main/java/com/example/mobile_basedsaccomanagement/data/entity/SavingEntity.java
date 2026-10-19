package com.example.mobile_basedsaccomanagement.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "savings",
    foreignKeys = @ForeignKey(
        entity = MemberEntity.class,
        parentColumns = "id",
        childColumns = "member_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index(value = {"member_id"}, unique = true)}
)
public class SavingEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "member_id")
    private long memberId;

    @ColumnInfo(name = "balance")
    private double balance;

    @ColumnInfo(name = "total_contributions")
    private double totalContributions;

    @ColumnInfo(name = "last_deposit_date")
    private String lastDepositDate;

    public SavingEntity() {}

    public SavingEntity(long memberId, double balance, double totalContributions, String lastDepositDate) {
        this.memberId = memberId;
        this.balance = balance;
        this.totalContributions = totalContributions;
        this.lastDepositDate = lastDepositDate;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getMemberId() { return memberId; }
    public void setMemberId(long memberId) { this.memberId = memberId; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public double getTotalContributions() { return totalContributions; }
    public void setTotalContributions(double totalContributions) { this.totalContributions = totalContributions; }

    public String getLastDepositDate() { return lastDepositDate; }
    public void setLastDepositDate(String lastDepositDate) { this.lastDepositDate = lastDepositDate; }
}
