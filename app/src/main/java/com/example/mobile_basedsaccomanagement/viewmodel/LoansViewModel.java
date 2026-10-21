package com.example.mobile_basedsaccomanagement.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobile_basedsaccomanagement.data.entity.LoanEntity;
import com.example.mobile_basedsaccomanagement.data.entity.LoanRepaymentEntity;
import com.example.mobile_basedsaccomanagement.data.entity.SaccoProductEntity;
import com.example.mobile_basedsaccomanagement.data.entity.SavingEntity;
import com.example.mobile_basedsaccomanagement.data.entity.TransactionEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;
import com.example.mobile_basedsaccomanagement.util.Constants;
import com.example.mobile_basedsaccomanagement.util.DateUtils;
import com.example.mobile_basedsaccomanagement.util.LoanCalculator;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoansViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final ExecutorService executorService;

    private final MutableLiveData<String> actionResult = new MutableLiveData<>();

    public LoansViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<String> getActionResult() { return actionResult; }

    public LiveData<List<SaccoProductEntity>> getAllProductsLiveData() {
        return db.saccoProductDao().getAllProductsLiveData();
    }

    public LiveData<List<LoanEntity>> getLoansByMemberIdLiveData(long memberId) {
        return db.loanDao().getLoansByMemberIdLiveData(memberId);
    }

    public LiveData<List<LoanEntity>> getAllLoansLiveData() {
        return db.loanDao().getAllLoansLiveData();
    }

    public LiveData<List<LoanRepaymentEntity>> getRepaymentsForLoanLiveData(long loanId) {
        return db.loanRepaymentDao().getRepaymentsForLoanLiveData(loanId);
    }

    public void applyForLoan(long memberId, long productId, double requestedAmount,
                             int durationMonths, String purpose, String repaymentFrequency) {
        executorService.execute(() -> {
            // 1. Check max loan limit: 3x Savings - active outstanding loans
            SavingEntity saving = db.savingDao().getSavingByMemberId(memberId);
            double savingsBal = saving != null ? saving.getBalance() : 0.0;

            List<LoanEntity> existingLoans = db.loanDao().getLoansByMemberId(memberId);
            double activeOutstanding = 0.0;
            for (LoanEntity l : existingLoans) {
                if (Constants.STATUS_ACTIVE.equalsIgnoreCase(l.getStatus())) {
                    activeOutstanding += l.getOutstandingBalance();
                }
            }

            double availableLimit = (savingsBal * 3.0) - activeOutstanding;
            if (availableLimit < 0) availableLimit = 0.0;

            if (requestedAmount > availableLimit) {
                actionResult.postValue("Application Failed! Requested amount exceeds your available loan limit of KES " + availableLimit);
                return;
            }

            SaccoProductEntity product = db.saccoProductDao().getProductById(productId);
            double interestRate = product != null ? product.getInterestRate() : 10.0;

            // 2. Calculate schedule
            LoanCalculator.LoanCalculationResult calc = LoanCalculator.calculateFlatRateLoan(requestedAmount, interestRate, durationMonths);

            String loanNo = "LN-" + (1000 + (int)(Math.random() * 9000));
            String currentDate = DateUtils.getCurrentDate();

            LoanEntity newLoan = new LoanEntity(
                    memberId,
                    productId,
                    loanNo,
                    calc.principal,
                    calc.interestRate,
                    calc.totalRepayment,
                    calc.monthlyInstallment,
                    calc.durationMonths,
                    purpose,
                    repaymentFrequency,
                    0.0,
                    calc.totalRepayment,
                    Constants.STATUS_PENDING,
                    currentDate,
                    null
            );

            db.loanDao().insertLoan(newLoan);
            actionResult.postValue("Loan Application " + loanNo + " submitted successfully! Pending admin approval.");
        });
    }

    public void makeRepayment(long loanId, long memberId, double amountPaid, String paymentMethod, String refCode) {
        executorService.execute(() -> {
            LoanEntity loan = db.loanDao().getLoanById(loanId);
            if (loan == null || !Constants.STATUS_ACTIVE.equalsIgnoreCase(loan.getStatus())) {
                actionResult.postValue("Repayment Failed! Active loan not found.");
                return;
            }

            double newRepaid = loan.getAmountRepaid() + amountPaid;
            double newOutstanding = loan.getOutstandingBalance() - amountPaid;
            if (newOutstanding < 0) newOutstanding = 0.0;

            String newStatus = newOutstanding == 0.0 ? Constants.STATUS_FULLY_PAID : Constants.STATUS_ACTIVE;
            String currentDate = DateUtils.getCurrentDate();

            loan.setAmountRepaid(newRepaid);
            loan.setOutstandingBalance(newOutstanding);
            loan.setStatus(newStatus);

            db.loanDao().updateLoan(loan);

            // Record Repayment
            db.loanRepaymentDao().insertRepayment(new LoanRepaymentEntity(
                    loanId, memberId, amountPaid, currentDate, paymentMethod, refCode
            ));

            // Log Transaction
            String txCode = "TXN-" + System.currentTimeMillis();
            db.transactionDao().insertTransaction(new TransactionEntity(
                    txCode, memberId, "LOAN_REPAYMENT", amountPaid, currentDate, DateUtils.getCurrentTime(), "Repayment for Loan " + loan.getLoanNumber() + " [Ref: " + refCode + "]", "COMPLETED"
            ));

            if (newOutstanding == 0.0) {
                actionResult.postValue("Congratulations! Loan " + loan.getLoanNumber() + " is FULLY PAID!");
            } else {
                actionResult.postValue("Repayment of KES " + amountPaid + " received for Loan " + loan.getLoanNumber() + ". Outstanding: KES " + newOutstanding);
            }
        });
    }
}
