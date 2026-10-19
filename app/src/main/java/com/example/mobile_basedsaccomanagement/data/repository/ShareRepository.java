package com.example.mobile_basedsaccomanagement.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.mobile_basedsaccomanagement.data.dao.ShareDao;
import com.example.mobile_basedsaccomanagement.data.entity.ShareEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShareRepository {

    private final ShareDao shareDao;
    private final ExecutorService executorService;

    public interface ShareCallback<T> {
        void onResult(T result);
    }

    public ShareRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        shareDao = db.shareDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<ShareEntity> getShareByMemberIdLiveData(long memberId) {
        return shareDao.getShareByMemberIdLiveData(memberId);
    }

    public void getShareByMemberId(long memberId, ShareCallback<ShareEntity> callback) {
        executorService.execute(() -> {
            ShareEntity share = shareDao.getShareByMemberId(memberId);
            if (callback != null) callback.onResult(share);
        });
    }

    public void updateShare(ShareEntity share, ShareCallback<Boolean> callback) {
        executorService.execute(() -> {
            int rows = shareDao.updateShare(share);
            if (callback != null) callback.onResult(rows > 0);
        });
    }
}
