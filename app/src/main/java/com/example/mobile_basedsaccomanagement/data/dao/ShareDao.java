package com.example.mobile_basedsaccomanagement.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobile_basedsaccomanagement.data.entity.ShareEntity;

@Dao
public interface ShareDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertShare(ShareEntity share);

    @Update
    int updateShare(ShareEntity share);

    @Query("SELECT * FROM shares WHERE member_id = :memberId LIMIT 1")
    ShareEntity getShareByMemberId(long memberId);

    @Query("SELECT * FROM shares WHERE member_id = :memberId LIMIT 1")
    LiveData<ShareEntity> getShareByMemberIdLiveData(long memberId);
}
