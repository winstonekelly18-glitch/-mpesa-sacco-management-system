package com.example.mobile_basedsaccomanagement.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.mobile_basedsaccomanagement.data.dao.LoanDao;
import com.example.mobile_basedsaccomanagement.data.dao.LoanRepaymentDao;
import com.example.mobile_basedsaccomanagement.data.entity.LoanEntity;
import com.example.mobile_basedsaccomanagement.data.entity.LoanRepaymentEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoanRepository {

    private final LoanDao loanDao;
    private final LoanRepaymentDao loanRepaymentDao;
    private final ExecutorService executorService;

    public interface LoanCallback<T> {
        void onResult(T result);
    }

    public LoanRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        loanDao = db.loanDao();
        loanRepaymentDao = db.loanRepaymentDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public void insertLoan(LoanEntity loan, LoanCallback<Long> callback) {
        executorService.execute(() -> {
            long id = loanDao.insertLoan(loan);
            if (callback != null) callback.onResult(id);
        });
    }

    public LiveData<List<LoanEntity>> getLoansByMemberIdLiveData(long memberId) {
        return loanDao.getLoansByMemberIdLiveData(memberId);
    }

    public LiveData<List<LoanEntity>> getAllLoansLiveData() {
        return loanDao.getAllLoansLiveData();
    }

    public void updateLoanStatus(long loanId, String status, String approvedDate, LoanCallback<Boolean> callback) {
        executorService.execute(() -> {
            int rows = loanDao.updateLoanStatus(loanId, status, approvedDate);
            if (callback != null) callback.onResult(rows > 0);
        });
    }

    public void insertRepayment(LoanRepaymentEntity repayment, LoanCallback<Long> callback) {
        executorService.execute(() -> {
            long id = loanRepaymentDao.insertRepayment(repayment);
            if (callback != null) callback.onResult(id);
        });
    }

    public LiveData<List<LoanRepaymentEntity>> getRepaymentsForLoanLiveData(long loanId) {
        return loanRepaymentDao.getRepaymentsForLoanLiveData(loanId);
    }

    public LiveData<Double> getTotalActiveLoansAmountLiveData() {
        return loanDao.getTotalActiveLoansAmountLiveData();
    }

    public LiveData<Integer> getPendingLoansCountLiveData() {
        return loanDao.getPendingLoansCountLiveData();
    }
}
