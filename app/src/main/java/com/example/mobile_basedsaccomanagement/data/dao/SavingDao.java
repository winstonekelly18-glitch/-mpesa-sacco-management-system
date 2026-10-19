package com.example.mobile_basedsaccomanagement.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobile_basedsaccomanagement.data.entity.SavingEntity;

@Dao
public interface SavingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSaving(SavingEntity saving);

    @Update
    int updateSaving(SavingEntity saving);

    @Query("SELECT * FROM savings WHERE member_id = :memberId LIMIT 1")
    SavingEntity getSavingByMemberId(long memberId);

    @Query("SELECT * FROM savings WHERE member_id = :memberId LIMIT 1")
    LiveData<SavingEntity> getSavingByMemberIdLiveData(long memberId);

    @Query("SELECT SUM(balance) FROM savings")
    LiveData<Double> getTotalSavingsPoolLiveData();

    @Query("SELECT SUM(balance) FROM savings")
    Double getTotalSavingsPool();
}
