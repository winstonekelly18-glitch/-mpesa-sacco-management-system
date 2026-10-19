package com.example.mobile_basedsaccomanagement.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobile_basedsaccomanagement.data.entity.TransactionEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;

import java.util.List;

public class TransactionsViewModel extends AndroidViewModel {

    private final AppDatabase db;

    public TransactionsViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<List<TransactionEntity>> getTransactionsByMemberIdLiveData(long memberId) {
        return db.transactionDao().getTransactionsByMemberIdLiveData(memberId);
    }
}
