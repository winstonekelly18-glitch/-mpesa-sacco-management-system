package com.example.mobile_basedsaccomanagement.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobile_basedsaccomanagement.adapters.LoanAdapter;
import com.example.mobile_basedsaccomanagement.data.entity.LoanEntity;
import com.example.mobile_basedsaccomanagement.databinding.ActivityLoanListBinding;
import com.example.mobile_basedsaccomanagement.models.Loan;
import com.example.mobile_basedsaccomanagement.util.Constants;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;
import com.example.mobile_basedsaccomanagement.viewmodel.LoansViewModel;

import java.util.ArrayList;
import java.util.List;

public class LoanListActivity extends AppCompatActivity {

    private ActivityLoanListBinding binding;
    private LoansViewModel loansViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoanListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        loansViewModel = new ViewModelProvider(this).get(LoansViewModel.class);

        binding.rvLoans.setLayoutManager(new LinearLayoutManager(this));

        long memberId = sessionManager.getUserId();

        observeViewModel(memberId);
    }

    private void observeViewModel(long memberId) {
        loansViewModel.getLoansByMemberIdLiveData(memberId).observe(this, loanEntities -> {
            List<Loan> list = new ArrayList<>();
            if (loanEntities != null) {
                for (LoanEntity e : loanEntities) {
                    list.add(new Loan(
                            e.getId(), e.getLoanNumber(), memberId, e.getPurpose(), e.getRequestedAmount(),
                            e.getInterestRate(), e.getTotalRepayment(), e.getMonthlyInstallment(), e.getDurationMonths(),
                            e.getPurpose(), e.getRepaymentFrequency(), e.getAmountRepaid(), e.getOutstandingBalance(),
                            e.getStatus(), e.getAppliedDate(), e.getApprovedDate()
                    ));
                }
            }

            LoanAdapter adapter = new LoanAdapter(this, list, loan -> {
                if (Constants.STATUS_ACTIVE.equalsIgnoreCase(loan.getStatus())) {
                    Intent intent = new Intent(LoanListActivity.this, LoanRepaymentActivity.class);
                    intent.putExtra("LOAN_ID", loan.getId());
                    startActivity(intent);
                }
            });
            binding.rvLoans.setAdapter(adapter);
        });
    }
}
