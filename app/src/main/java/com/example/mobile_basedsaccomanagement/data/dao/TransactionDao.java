package com.example.mobile_basedsaccomanagement.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobile_basedsaccomanagement.data.entity.TransactionEntity;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTransaction(TransactionEntity transaction);

    @Query("SELECT * FROM transactions WHERE member_id = :memberId ORDER BY id DESC")
    LiveData<List<TransactionEntity>> getTransactionsByMemberIdLiveData(long memberId);

    @Query("SELECT * FROM transactions WHERE member_id = :memberId ORDER BY id DESC")
    List<TransactionEntity> getTransactionsByMemberId(long memberId);

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    LiveData<List<TransactionEntity>> getAllTransactionsLiveData();

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    List<TransactionEntity> getAllTransactions();

    @Query("SELECT COUNT(*) FROM transactions")
    LiveData<Integer> getTransactionCountLiveData();
}
