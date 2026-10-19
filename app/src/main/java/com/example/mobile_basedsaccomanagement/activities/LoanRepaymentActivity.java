package com.example.mobile_basedsaccomanagement.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobile_basedsaccomanagement.data.entity.LoanEntity;
import com.example.mobile_basedsaccomanagement.databinding.ActivityLoanRepaymentBinding;
import com.example.mobile_basedsaccomanagement.util.Constants;
import com.example.mobile_basedsaccomanagement.util.Validators;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;
import com.example.mobile_basedsaccomanagement.viewmodel.LoansViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoanRepaymentActivity extends AppCompatActivity {

    private ActivityLoanRepaymentBinding binding;
    private LoansViewModel loansViewModel;
    private SessionManager sessionManager;
    private long memberId;

    private final List<LoanEntity> activeLoansList = new ArrayList<>();
    private LoanEntity selectedLoan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoanRepaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        memberId = sessionManager.getUserId();

        loansViewModel = new ViewModelProvider(this).get(LoansViewModel.class);

        setupMethodsSpinner();
        observeViewModel();

        binding.spActiveLoans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < activeLoansList.size()) {
                    selectedLoan = activeLoansList.get(position);
                    updateLoanDetailsUI();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.btnSubmitRepayment.setOnClickListener(v -> performRepayment());
    }

    private void setupMethodsSpinner() {
        String[] methods = {"M-Pesa Mobile Money", "Bank Wire Transfer", "SACCO Branch Cash Deposit"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, methods);
        binding.spPaymentMethod.setAdapter(adapter);
    }

    private void observeViewModel() {
        loansViewModel.getLoansByMemberIdLiveData(memberId).observe(this, loans -> {
            activeLoansList.clear();
            List<String> loanSpinnerItems = new ArrayList<>();

            if (loans != null) {
                for (LoanEntity l : loans) {
                    if (Constants.STATUS_ACTIVE.equalsIgnoreCase(l.getStatus())) {
                        activeLoansList.add(l);
                        loanSpinnerItems.add(l.getLoanNumber() + " (Bal: KES " + String.format(Locale.getDefault(), "%,.2f", l.getOutstandingBalance()) + ")");
                    }
                }
            }

            if (activeLoansList.isEmpty()) {
                loanSpinnerItems.add("No Active Loans Found");
                binding.btnSubmitRepayment.setEnabled(false);
            } else {
                binding.btnSubmitRepayment.setEnabled(true);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, loanSpinnerItems);
            binding.spActiveLoans.setAdapter(adapter);

            if (!activeLoansList.isEmpty()) {
                selectedLoan = activeLoansList.get(0);
                updateLoanDetailsUI();
            }
        });

        loansViewModel.getActionResult().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                if (msg.contains("received") || msg.contains("FULLY PAID")) {
                    finish();
                }
            }
        });
    }

    private void updateLoanDetailsUI() {
        if (selectedLoan != null && binding != null) {
            binding.tvRepayOutstanding.setText("KES " + String.format(Locale.getDefault(), "%,.2f", selectedLoan.getOutstandingBalance()));
            binding.tvRepayInstallment.setText("KES " + String.format(Locale.getDefault(), "%,.2f", selectedLoan.getMonthlyInstallment()));
            binding.tvRepayDueDate.setText(selectedLoan.getApprovedDate() != null ? selectedLoan.getApprovedDate() : "N/A");
            binding.etRepayAmount.setText(String.format(Locale.getDefault(), "%.2f", selectedLoan.getMonthlyInstallment()));
        }
    }

    private void performRepayment() {
        if (selectedLoan == null) {
            Toast.makeText(this, "No active loan selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String amtStr = binding.etRepayAmount.getText().toString().trim();
        String ref = binding.etRefCode.getText().toString().trim();
        String method = binding.spPaymentMethod.getSelectedItem().toString();

        if (!Validators.isPositiveNumber(amtStr)) {
            binding.etRepayAmount.setError("Enter valid repayment amount > 0");
            return;
        }

        double amount = Double.parseDouble(amtStr);
        if (amount > selectedLoan.getOutstandingBalance()) {
            Toast.makeText(this, "Repayment amount cannot exceed outstanding balance of KES " + selectedLoan.getOutstandingBalance(), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(ref)) {
            ref = "MP" + System.currentTimeMillis() % 1000000;
        }

        loansViewModel.makeRepayment(selectedLoan.getId(), memberId, amount, method, ref);
    }
}
