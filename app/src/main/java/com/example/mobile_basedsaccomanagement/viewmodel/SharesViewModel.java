package com.example.mobile_basedsaccomanagement.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobile_basedsaccomanagement.data.entity.ShareEntity;
import com.example.mobile_basedsaccomanagement.data.entity.TransactionEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;
import com.example.mobile_basedsaccomanagement.util.DateUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SharesViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final ExecutorService executorService;
    private final MutableLiveData<String> actionResult = new MutableLiveData<>();

    public SharesViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<String> getActionResult() { return actionResult; }

    public LiveData<ShareEntity> getShareByMemberIdLiveData(long memberId) {
        return db.shareDao().getShareByMemberIdLiveData(memberId);
    }

    public void buyShares(long memberId, int numberOfShares) {
        executorService.execute(() -> {
            ShareEntity current = db.shareDao().getShareByMemberId(memberId);
            double shareValue = 100.0;
            double cost = numberOfShares * shareValue;

            if (current == null) {
                current = new ShareEntity(memberId, 0, shareValue, 0.0, 100, "BELOW_MINIMUM");
                db.shareDao().insertShare(current);
                current = db.shareDao().getShareByMemberId(memberId);
            }

            int newTotal = current.getTotalShares() + numberOfShares;
            double newContrib = current.getTotalContribution() + cost;
            String status = newTotal >= current.getMinRequiredShares() ? "COMPLIANT" : "BELOW_MINIMUM";

            current.setTotalShares(newTotal);
            current.setTotalContribution(newContrib);
            current.setStatus(status);

            db.shareDao().updateShare(current);

            // Log Transaction
            String date = DateUtils.getCurrentDate();
            String txCode = "TXN-" + System.currentTimeMillis();
            db.transactionDao().insertTransaction(new TransactionEntity(
                    txCode, memberId, "SHARE_CONTRIBUTION", cost, date, DateUtils.getCurrentTime(), "Purchased " + numberOfShares + " SACCO Shares", "COMPLETED"
            ));

            actionResult.postValue("Purchased " + numberOfShares + " SACCO shares worth KES " + cost + " successfully!");
        });
    }
}
