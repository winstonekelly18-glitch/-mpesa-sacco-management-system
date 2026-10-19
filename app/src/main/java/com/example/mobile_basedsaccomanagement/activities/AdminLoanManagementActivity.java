package com.example.mobile_basedsaccomanagement.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobile_basedsaccomanagement.adapters.LoanAdapter;
import com.example.mobile_basedsaccomanagement.data.entity.LoanEntity;
import com.example.mobile_basedsaccomanagement.databinding.ActivityAdminLoanManagementBinding;
import com.example.mobile_basedsaccomanagement.models.Loan;
import com.example.mobile_basedsaccomanagement.util.Constants;
import com.example.mobile_basedsaccomanagement.viewmodel.AdminViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminLoanManagementActivity extends AppCompatActivity {

    private ActivityAdminLoanManagementBinding binding;
    private AdminViewModel adminViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoanManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        binding.rvAdminLoans.setLayoutManager(new LinearLayoutManager(this));

        observeViewModel();
    }

    private void observeViewModel() {
        adminViewModel.getAllLoansLiveData().observe(this, loanEntities -> {
            List<Loan> list = new ArrayList<>();
            if (loanEntities != null) {
                for (LoanEntity e : loanEntities) {
                    list.add(new Loan(
                            e.getId(), e.getLoanNumber(), e.getMemberId(), e.getPurpose(), e.getRequestedAmount(),
                            e.getInterestRate(), e.getTotalRepayment(), e.getMonthlyInstallment(), e.getDurationMonths(),
                            e.getPurpose(), e.getRepaymentFrequency(), e.getAmountRepaid(), e.getOutstandingBalance(),
                            e.getStatus(), e.getAppliedDate(), e.getApprovedDate()
                    ));
                }
            }

            LoanAdapter adapter = new LoanAdapter(this, list, loan -> {
                if (Constants.STATUS_PENDING.equalsIgnoreCase(loan.getStatus())) {
                    showLoanApprovalDialog(loan);
                } else {
                    Toast.makeText(this, "Loan " + loan.getLoanNumber() + " status: " + loan.getStatus(), Toast.LENGTH_SHORT).show();
                }
            });

            binding.rvAdminLoans.setAdapter(adapter);
        });

        adminViewModel.getActionResult().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoanApprovalDialog(Loan loan) {
        new AlertDialog.Builder(this)
                .setTitle("Review Loan " + loan.getLoanNumber())
                .setMessage("Requested Amount: KES " + loan.getRequestedAmount() + "\nPurpose: " + loan.getPurpose() + "\nDuration: " + loan.getDurationMonths() + " Months")
                .setPositiveButton("Approve and Disburse", (dialog, which) -> {
                    adminViewModel.approveLoanAndDisburse(loan.getId());
                })
                .setNegativeButton("Reject Application", (dialog, which) -> {
                    adminViewModel.rejectLoan(loan.getId());
                })
                .setNeutralButton("Cancel", null)
                .show();
    }
}
