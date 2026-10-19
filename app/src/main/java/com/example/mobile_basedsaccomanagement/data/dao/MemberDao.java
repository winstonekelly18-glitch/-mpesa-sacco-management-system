package com.example.mobile_basedsaccomanagement.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobile_basedsaccomanagement.data.entity.MemberEntity;

import java.util.List;

@Dao
public interface MemberDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertMember(MemberEntity member);

    @Update
    int updateMember(MemberEntity member);

    @Query("SELECT * FROM members WHERE user_id = :userId LIMIT 1")
    MemberEntity findByUserId(long userId);

    @Query("SELECT * FROM members WHERE user_id = :userId LIMIT 1")
    LiveData<MemberEntity> findByUserIdLiveData(long userId);

    @Query("SELECT * FROM members WHERE id = :memberId LIMIT 1")
    MemberEntity findById(long memberId);

    @Query("SELECT * FROM members WHERE member_number = :memberNo LIMIT 1")
    MemberEntity findByMemberNumber(String memberNo);

    @Query("SELECT * FROM members ORDER BY id DESC")
    LiveData<List<MemberEntity>> getAllMembersLiveData();

    @Query("SELECT * FROM members ORDER BY id DESC")
    List<MemberEntity> getAllMembers();

    @Query("UPDATE members SET status = :status WHERE id = :memberId")
    int updateMemberStatus(long memberId, String status);

    @Query("SELECT COUNT(*) FROM members WHERE status = 'PENDING'")
    LiveData<Integer> getPendingMembersCountLiveData();
}
