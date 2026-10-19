package com.example.mobile_basedsaccomanagement.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobile_basedsaccomanagement.data.entity.AnnouncementEntity;
import com.example.mobile_basedsaccomanagement.data.entity.LoanEntity;
import com.example.mobile_basedsaccomanagement.data.entity.MemberEntity;
import com.example.mobile_basedsaccomanagement.data.entity.NotificationEntity;
import com.example.mobile_basedsaccomanagement.data.entity.SaccoProductEntity;
import com.example.mobile_basedsaccomanagement.data.entity.SavingEntity;
import com.example.mobile_basedsaccomanagement.data.entity.TransactionEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;
import com.example.mobile_basedsaccomanagement.util.Constants;
import com.example.mobile_basedsaccomanagement.util.DateUtils;
import com.example.mobile_basedsaccomanagement.util.NotificationHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final ExecutorService executorService;
    private final MutableLiveData<String> actionResult = new MutableLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<String> getActionResult() { return actionResult; }

    // Metrics LiveData
    public LiveData<Double> getTotalSavingsPoolLiveData() { return db.savingDao().getTotalSavingsPoolLiveData(); }
    public LiveData<Double> getTotalActiveLoansAmountLiveData() { return db.loanDao().getTotalActiveLoansAmountLiveData(); }
    public LiveData<Integer> getPendingLoansCountLiveData() { return db.loanDao().getPendingLoansCountLiveData(); }
    public LiveData<Integer> getPendingMembersCountLiveData() { return db.memberDao().getPendingMembersCountLiveData(); }
    public LiveData<List<MemberEntity>> getAllMembersLiveData() { return db.memberDao().getAllMembersLiveData(); }
    public LiveData<List<LoanEntity>> getAllLoansLiveData() { return db.loanDao().getAllLoansLiveData(); }
    public LiveData<List<SaccoProductEntity>> getAllProductsLiveData() { return db.saccoProductDao().getAllProductsLiveData(); }
    public LiveData<List<AnnouncementEntity>> getAllAnnouncementsLiveData() { return db.announcementDao().getAllAnnouncementsLiveData(); }
    public LiveData<List<TransactionEntity>> getAllTransactionsLiveData() { return db.transactionDao().getAllTransactionsLiveData(); }

    public void updateMemberStatus(long memberId, String status) {
        executorService.execute(() -> {
            int rows = db.memberDao().updateMemberStatus(memberId, status);
            if (rows > 0) {
                MemberEntity member = db.memberDao().findById(memberId);
                String msg = "Your SACCO account status has been updated to: " + status;
                db.notificationDao().insertNotification(new NotificationEntity(
                        memberId, "Account Status Update", msg, DateUtils.getCurrentTimestamp(), false
                ));
                NotificationHelper.sendSystemNotification(getApplication(), "SACCO Account Update", msg);
                actionResult.postValue("Member status updated to " + status);
            }
        });
    }

    public void approveLoanAndDisburse(long loanId) {
        executorService.execute(() -> {
            LoanEntity loan = db.loanDao().getLoanById(loanId);
            if (loan == null) return;

            String date = DateUtils.getCurrentDate();
            int rows = db.loanDao().updateLoanStatus(loanId, Constants.STATUS_ACTIVE, date);

            if (rows > 0) {
                // 1. Disburse funds into Member Savings
                SavingEntity saving = db.savingDao().getSavingByMemberId(loan.getMemberId());
                if (saving != null) {
                    saving.setBalance(saving.getBalance() + loan.getRequestedAmount());
                    db.savingDao().updateSaving(saving);
                }

                // 2. Log Disbursement Transaction
                String txCode = "TXN-" + System.currentTimeMillis();
                db.transactionDao().insertTransaction(new TransactionEntity(
                        txCode, loan.getMemberId(), "LOAN_DISBURSEMENT", loan.getRequestedAmount(), date, DateUtils.getCurrentTime(), "Loan Disbursement for " + loan.getLoanNumber(), "COMPLETED"
                ));

                // 3. Send In-App & System Notification
                String title = "Loan Approved & Disbursed!";
                String msg = "Your loan " + loan.getLoanNumber() + " of KES " + loan.getRequestedAmount() + " has been approved & credited to your savings balance.";
                db.notificationDao().insertNotification(new NotificationEntity(
                        loan.getMemberId(), title, msg, DateUtils.getCurrentTimestamp(), false
                ));
                NotificationHelper.sendSystemNotification(getApplication(), title, msg);

                actionResult.postValue("Loan " + loan.getLoanNumber() + " Approved & Disbursed Successfully!");
            }
        });
    }

    public void rejectLoan(long loanId) {
        executorService.execute(() -> {
            LoanEntity loan = db.loanDao().getLoanById(loanId);
            if (loan == null) return;

            db.loanDao().updateLoanStatus(loanId, Constants.STATUS_REJECTED, DateUtils.getCurrentDate());

            String title = "Loan Application Status";
            String msg = "Your loan application " + loan.getLoanNumber() + " was rejected by administration.";
            db.notificationDao().insertNotification(new NotificationEntity(
                    loan.getMemberId(), title, msg, DateUtils.getCurrentTimestamp(), false
            ));

            actionResult.postValue("Loan " + loan.getLoanNumber() + " Rejected.");
        });
    }

    public void addProduct(String name, double rate, double min, double max, int maxMonths, String calcType) {
        executorService.execute(() -> {
            SaccoProductEntity prod = new SaccoProductEntity(name, rate, min, max, maxMonths, calcType);
            db.saccoProductDao().insertProduct(prod);
            actionResult.postValue("SACCO Product '" + name + "' added successfully!");
        });
    }

    public void broadcastAnnouncement(String title, String content, String postedBy) {
        executorService.execute(() -> {
            AnnouncementEntity ann = new AnnouncementEntity(title, content, DateUtils.getCurrentDate(), postedBy);
            db.announcementDao().insertAnnouncement(ann);

            // Notify every member
            List<MemberEntity> members = db.memberDao().getAllMembers();
            for (MemberEntity m : members) {
                db.notificationDao().insertNotification(new NotificationEntity(
                        m.getId(), "Announcement: " + title, content, DateUtils.getCurrentTimestamp(), false
                ));
            }

            NotificationHelper.sendSystemNotification(getApplication(), "SACCO Announcement", title);
            actionResult.postValue("Announcement broadcasted to all members!");
        });
    }
}
