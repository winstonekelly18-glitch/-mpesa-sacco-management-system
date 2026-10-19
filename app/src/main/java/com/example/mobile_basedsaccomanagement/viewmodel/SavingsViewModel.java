package com.example.mobile_basedsaccomanagement.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobile_basedsaccomanagement.data.entity.SavingEntity;
import com.example.mobile_basedsaccomanagement.data.entity.TransactionEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;
import com.example.mobile_basedsaccomanagement.util.DateUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SavingsViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final ExecutorService executorService;
    private final MutableLiveData<String> actionResult = new MutableLiveData<>();

    public SavingsViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<String> getActionResult() { return actionResult; }

    public LiveData<SavingEntity> getSavingByMemberIdLiveData(long memberId) {
        return db.savingDao().getSavingByMemberIdLiveData(memberId);
    }

    public LiveData<List<TransactionEntity>> getSavingsTransactionsLiveData(long memberId) {
        return db.transactionDao().getTransactionsByMemberIdLiveData(memberId);
    }

    public void depositSavings(long memberId, double amount, String description) {
        executorService.execute(() -> {
            SavingEntity current = db.savingDao().getSavingByMemberId(memberId);
            if (current == null) {
                current = new SavingEntity(memberId, 0.0, 0.0, DateUtils.getCurrentDate());
                db.savingDao().insertSaving(current);
                current = db.savingDao().getSavingByMemberId(memberId);
            }

            double newBal = current.getBalance() + amount;
            double newContrib = current.getTotalContributions() + amount;
            String date = DateUtils.getCurrentDate();

            current.setBalance(newBal);
            current.setTotalContributions(newContrib);
            current.setLastDepositDate(date);

            db.savingDao().updateSaving(current);

            // Log Transaction
            String txCode = "TXN-" + System.currentTimeMillis();
            db.transactionDao().insertTransaction(new TransactionEntity(
                    txCode, memberId, "DEPOSIT", amount, date, DateUtils.getCurrentTime(), description, "COMPLETED"
            ));

            actionResult.postValue("Successfully deposited KES " + amount + " to savings!");
        });
    }

    public void withdrawSavings(long memberId, double amount, String description) {
        executorService.execute(() -> {
            SavingEntity current = db.savingDao().getSavingByMemberId(memberId);
            if (current == null || current.getBalance() < amount) {
                actionResult.postValue("Withdrawal failed! Insufficient savings balance.");
                return;
            }

            double newBal = current.getBalance() - amount;
            String date = DateUtils.getCurrentDate();

            current.setBalance(newBal);
            db.savingDao().updateSaving(current);

            // Log Transaction
            String txCode = "TXN-" + System.currentTimeMillis();
            db.transactionDao().insertTransaction(new TransactionEntity(
                    txCode, memberId, "WITHDRAWAL", amount, date, DateUtils.getCurrentTime(), description, "COMPLETED"
            ));

            actionResult.postValue("Successfully withdrew KES " + amount + " from savings.");
        });
    }
}
