package com.example.mobile_basedsaccomanagement.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.mobile_basedsaccomanagement.data.dao.SavingDao;
import com.example.mobile_basedsaccomanagement.data.entity.SavingEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SavingRepository {

    private final SavingDao savingDao;
    private final ExecutorService executorService;

    public interface SavingCallback<T> {
        void onResult(T result);
    }

    public SavingRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        savingDao = db.savingDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<SavingEntity> getSavingByMemberIdLiveData(long memberId) {
        return savingDao.getSavingByMemberIdLiveData(memberId);
    }

    public void getSavingByMemberId(long memberId, SavingCallback<SavingEntity> callback) {
        executorService.execute(() -> {
            SavingEntity saving = savingDao.getSavingByMemberId(memberId);
            if (callback != null) callback.onResult(saving);
        });
    }

    public void updateSaving(SavingEntity saving, SavingCallback<Boolean> callback) {
        executorService.execute(() -> {
            int rows = savingDao.updateSaving(saving);
            if (callback != null) callback.onResult(rows > 0);
        });
    }

    public LiveData<Double> getTotalSavingsPoolLiveData() {
        return savingDao.getTotalSavingsPoolLiveData();
    }
}
