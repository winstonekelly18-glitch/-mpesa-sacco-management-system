package com.example.mobile_basedsaccomanagement.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.mobile_basedsaccomanagement.data.dao.TransactionDao;
import com.example.mobile_basedsaccomanagement.data.entity.TransactionEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRepository {

    private final TransactionDao transactionDao;
    private final ExecutorService executorService;

    public interface TransactionCallback<T> {
        void onResult(T result);
    }

    public TransactionRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        transactionDao = db.transactionDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public void insertTransaction(TransactionEntity transaction, TransactionCallback<Long> callback) {
        executorService.execute(() -> {
            long id = transactionDao.insertTransaction(transaction);
            if (callback != null) callback.onResult(id);
        });
    }

    public LiveData<List<TransactionEntity>> getTransactionsByMemberIdLiveData(long memberId) {
        return transactionDao.getTransactionsByMemberIdLiveData(memberId);
    }

    public LiveData<List<TransactionEntity>> getAllTransactionsLiveData() {
        return transactionDao.getAllTransactionsLiveData();
    }

    public LiveData<Integer> getTransactionCountLiveData() {
        return transactionDao.getTransactionCountLiveData();
    }
}
