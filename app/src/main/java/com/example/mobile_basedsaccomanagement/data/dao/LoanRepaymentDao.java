package com.example.mobile_basedsaccomanagement.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobile_basedsaccomanagement.data.entity.LoanRepaymentEntity;

import java.util.List;

@Dao
public interface LoanRepaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRepayment(LoanRepaymentEntity repayment);

    @Query("SELECT * FROM loan_repayments WHERE loan_id = :loanId ORDER BY id DESC")
    LiveData<List<LoanRepaymentEntity>> getRepaymentsForLoanLiveData(long loanId);

    @Query("SELECT * FROM loan_repayments WHERE loan_id = :loanId ORDER BY id DESC")
    List<LoanRepaymentEntity> getRepaymentsForLoan(long loanId);
}
