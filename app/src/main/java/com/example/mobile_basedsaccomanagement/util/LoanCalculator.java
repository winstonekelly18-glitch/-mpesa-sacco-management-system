package com.example.mobile_basedsaccomanagement.util;

public class LoanCalculator {

    public static class LoanCalculationResult {
        public double principal;
        public double interestRate;
        public double totalInterest;
        public double totalRepayment;
        public double monthlyInstallment;
        public int durationMonths;

        public LoanCalculationResult(double principal, double interestRate, double totalInterest,
                                     double totalRepayment, double monthlyInstallment, int durationMonths) {
            this.principal = principal;
            this.interestRate = interestRate;
            this.totalInterest = totalInterest;
            this.totalRepayment = totalRepayment;
            this.monthlyInstallment = monthlyInstallment;
            this.durationMonths = durationMonths;
        }
    }

    public static LoanCalculationResult calculateFlatRateLoan(double principal, double annualInterestRate, int durationMonths) {
        if (durationMonths <= 0) durationMonths = 1;
        double totalInterest = principal * (annualInterestRate / 100.0) * (durationMonths / 12.0);
        double totalRepayment = principal + totalInterest;
        double monthlyInstallment = totalRepayment / durationMonths;
        return new LoanCalculationResult(principal, annualInterestRate, totalInterest, totalRepayment, monthlyInstallment, durationMonths);
    }
}
