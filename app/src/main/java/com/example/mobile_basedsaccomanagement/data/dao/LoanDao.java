package com.example.mobile_basedsaccomanagement.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobile_basedsaccomanagement.data.entity.LoanEntity;

import java.util.List;

@Dao
public interface LoanDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertLoan(LoanEntity loan);

    @Update
    int updateLoan(LoanEntity loan);

    @Query("SELECT * FROM loans WHERE id = :loanId LIMIT 1")
    LoanEntity getLoanById(long loanId);

    @Query("SELECT * FROM loans WHERE id = :loanId LIMIT 1")
    LiveData<LoanEntity> getLoanByIdLiveData(long loanId);

    @Query("SELECT * FROM loans WHERE member_id = :memberId ORDER BY id DESC")
    LiveData<List<LoanEntity>> getLoansByMemberIdLiveData(long memberId);

    @Query("SELECT * FROM loans WHERE member_id = :memberId ORDER BY id DESC")
    List<LoanEntity> getLoansByMemberId(long memberId);

    @Query("SELECT * FROM loans ORDER BY id DESC")
    LiveData<List<LoanEntity>> getAllLoansLiveData();

    @Query("SELECT * FROM loans ORDER BY id DESC")
    List<LoanEntity> getAllLoans();

    @Query("SELECT SUM(outstanding_balance) FROM loans WHERE status = 'ACTIVE'")
    LiveData<Double> getTotalActiveLoansAmountLiveData();

    @Query("SELECT COUNT(*) FROM loans WHERE status = 'PENDING'")
    LiveData<Integer> getPendingLoansCountLiveData();

    @Query("SELECT COUNT(*) FROM loans WHERE status = 'PENDING'")
    int getPendingLoansCount();

    @Query("UPDATE loans SET status = :status, approved_date = :approvedDate WHERE id = :loanId")
    int updateLoanStatus(long loanId, String status, String approvedDate);
}
