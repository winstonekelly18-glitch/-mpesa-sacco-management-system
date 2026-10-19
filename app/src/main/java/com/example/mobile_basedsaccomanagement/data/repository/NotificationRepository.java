package com.example.mobile_basedsaccomanagement.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.mobile_basedsaccomanagement.data.dao.NotificationDao;
import com.example.mobile_basedsaccomanagement.data.entity.NotificationEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationRepository {

    private final NotificationDao notificationDao;
    private final ExecutorService executorService;

    public interface NotificationCallback<T> {
        void onResult(T result);
    }

    public NotificationRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        notificationDao = db.notificationDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public void insertNotification(NotificationEntity notification, NotificationCallback<Long> callback) {
        executorService.execute(() -> {
            long id = notificationDao.insertNotification(notification);
            if (callback != null) callback.onResult(id);
        });
    }

    public LiveData<List<NotificationEntity>> getNotificationsForMemberLiveData(long memberId) {
        return notificationDao.getNotificationsForMemberLiveData(memberId);
    }

    public LiveData<Integer> getUnreadCountLiveData(long memberId) {
        return notificationDao.getUnreadCountLiveData(memberId);
    }

    public void markAllAsRead(long memberId, NotificationCallback<Boolean> callback) {
        executorService.execute(() -> {
            int rows = notificationDao.markAllAsRead(memberId);
            if (callback != null) callback.onResult(rows > 0);
        });
    }

    public void deleteNotification(long notificationId, NotificationCallback<Boolean> callback) {
        executorService.execute(() -> {
            int rows = notificationDao.deleteNotification(notificationId);
            if (callback != null) callback.onResult(rows > 0);
        });
    }
}
