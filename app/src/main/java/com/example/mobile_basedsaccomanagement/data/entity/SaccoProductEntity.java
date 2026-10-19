package com.example.mobile_basedsaccomanagement.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sacco_products")
public class SaccoProductEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "product_name")
    private String productName;

    @ColumnInfo(name = "interest_rate")
    private double interestRate;

    @ColumnInfo(name = "min_amount")
    private double minAmount;

    @ColumnInfo(name = "max_amount")
    private double maxAmount;

    @ColumnInfo(name = "max_duration_months")
    private int maxDurationMonths;

    @ColumnInfo(name = "calculation_type")
    private String calculationType; // "FLAT" or "REDUCING_BALANCE"

    public SaccoProductEntity() {}

    public SaccoProductEntity(String productName, double interestRate, double minAmount,
                              double maxAmount, int maxDurationMonths, String calculationType) {
        this.productName = productName;
        this.interestRate = interestRate;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.maxDurationMonths = maxDurationMonths;
        this.calculationType = calculationType;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public double getMinAmount() { return minAmount; }
    public void setMinAmount(double minAmount) { this.minAmount = minAmount; }

    public double getMaxAmount() { return maxAmount; }
    public void setMaxAmount(double maxAmount) { this.maxAmount = maxAmount; }

    public int getMaxDurationMonths() { return maxDurationMonths; }
    public void setMaxDurationMonths(int maxDurationMonths) { this.maxDurationMonths = maxDurationMonths; }

    public String getCalculationType() { return calculationType; }
    public void setCalculationType(String calculationType) { this.calculationType = calculationType; }
}
