package com.example.mobile_basedsaccomanagement.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobile_basedsaccomanagement.data.entity.NotificationEntity;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNotification(NotificationEntity notification);

    @Query("SELECT * FROM notifications WHERE member_id = :memberId ORDER BY id DESC")
    LiveData<List<NotificationEntity>> getNotificationsForMemberLiveData(long memberId);

    @Query("SELECT COUNT(*) FROM notifications WHERE member_id = :memberId AND is_read = 0")
    LiveData<Integer> getUnreadCountLiveData(long memberId);

    @Query("UPDATE notifications SET is_read = 1 WHERE member_id = :memberId")
    int markAllAsRead(long memberId);

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    int deleteNotification(long notificationId);
}
