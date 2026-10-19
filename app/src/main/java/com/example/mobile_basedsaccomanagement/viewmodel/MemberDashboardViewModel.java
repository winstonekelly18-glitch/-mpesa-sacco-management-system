package com.example.mobile_basedsaccomanagement.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobile_basedsaccomanagement.data.entity.LoanEntity;
import com.example.mobile_basedsaccomanagement.data.entity.MemberEntity;
import com.example.mobile_basedsaccomanagement.data.entity.SavingEntity;
import com.example.mobile_basedsaccomanagement.data.entity.TransactionEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;

import java.util.List;

public class MemberDashboardViewModel extends AndroidViewModel {

    private final AppDatabase db;

    public MemberDashboardViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<MemberEntity> getMemberByUserIdLiveData(long userId) {
        return db.memberDao().findByUserIdLiveData(userId);
    }

    public LiveData<SavingEntity> getSavingByMemberIdLiveData(long memberId) {
        return db.savingDao().getSavingByMemberIdLiveData(memberId);
    }

    public LiveData<List<LoanEntity>> getLoansByMemberIdLiveData(long memberId) {
        return db.loanDao().getLoansByMemberIdLiveData(memberId);
    }

    public LiveData<List<TransactionEntity>> getRecentTransactionsLiveData(long memberId) {
        return db.transactionDao().getTransactionsByMemberIdLiveData(memberId);
    }

    public LiveData<Integer> getUnreadNotificationCountLiveData(long memberId) {
        return db.notificationDao().getUnreadCountLiveData(memberId);
    }
}
