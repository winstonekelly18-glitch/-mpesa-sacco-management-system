package com.example.mobile_basedsaccomanagement.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "shares",
    foreignKeys = @ForeignKey(
        entity = MemberEntity.class,
        parentColumns = "id",
        childColumns = "member_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index(value = {"member_id"}, unique = true)}
)
public class ShareEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "member_id")
    private long memberId;

    @ColumnInfo(name = "total_shares")
    private int totalShares;

    @ColumnInfo(name = "share_value")
    private double shareValue;

    @ColumnInfo(name = "total_contribution")
    private double totalContribution;

    @ColumnInfo(name = "min_required_shares")
    private int minRequiredShares;

    @ColumnInfo(name = "status")
    private String status; // "COMPLIANT" or "BELOW_MINIMUM"

    public ShareEntity() {}

    public ShareEntity(long memberId, int totalShares, double shareValue, double totalContribution,
                       int minRequiredShares, String status) {
        this.memberId = memberId;
        this.totalShares = totalShares;
        this.shareValue = shareValue;
        this.totalContribution = totalContribution;
        this.minRequiredShares = minRequiredShares;
        this.status = status;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getMemberId() { return memberId; }
    public void setMemberId(long memberId) { this.memberId = memberId; }

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
