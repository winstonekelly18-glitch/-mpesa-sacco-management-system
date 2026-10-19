package com.example.mobile_basedsaccomanagement.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobile_basedsaccomanagement.data.entity.AdminEntity;

@Dao
public interface AdminDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAdmin(AdminEntity admin);

    @Query("SELECT * FROM admins WHERE user_id = :userId LIMIT 1")
    AdminEntity findByUserId(long userId);
}
