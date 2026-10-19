package com.example.mobile_basedsaccomanagement.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobile_basedsaccomanagement.data.entity.AnnouncementEntity;

import java.util.List;

@Dao
public interface AnnouncementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAnnouncement(AnnouncementEntity announcement);

    @Query("SELECT * FROM announcements ORDER BY id DESC")
    LiveData<List<AnnouncementEntity>> getAllAnnouncementsLiveData();

    @Query("SELECT * FROM announcements ORDER BY id DESC")
    List<AnnouncementEntity> getAllAnnouncements();
}
