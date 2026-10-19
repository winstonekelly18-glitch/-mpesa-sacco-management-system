package com.example.mobile_basedsaccomanagement.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobile_basedsaccomanagement.data.entity.NotificationEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;
import com.example.mobile_basedsaccomanagement.util.NotificationHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationsViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final ExecutorService executorService;

    public NotificationsViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<List<NotificationEntity>> getNotificationsForMemberLiveData(long memberId) {
        return db.notificationDao().getNotificationsForMemberLiveData(memberId);
    }

    public LiveData<Integer> getUnreadCountLiveData(long memberId) {
        return db.notificationDao().getUnreadCountLiveData(memberId);
    }

    public void markAllAsRead(long memberId) {
        executorService.execute(() -> db.notificationDao().markAllAsRead(memberId));
    }

    public void deleteNotification(long notificationId) {
        executorService.execute(() -> db.notificationDao().deleteNotification(notificationId));
    }

    public void sendNotification(long memberId, String title, String message) {
        executorService.execute(() -> {
            NotificationEntity entity = new NotificationEntity(
                    memberId, title, message, com.example.mobile_basedsaccomanagement.util.DateUtils.getCurrentTimestamp(), false
            );
            db.notificationDao().insertNotification(entity);
            NotificationHelper.sendSystemNotification(getApplication(), title, message);
        });
    }
}
