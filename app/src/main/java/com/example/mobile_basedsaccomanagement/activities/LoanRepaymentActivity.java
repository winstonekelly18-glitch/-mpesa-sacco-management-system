package com.example.mobile_basedsaccomanagement.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobile_basedsaccomanagement.data.entity.LoanEntity;
import com.example.mobile_basedsaccomanagement.databinding.ActivityLoanRepaymentBinding;
import com.example.mobile_basedsaccomanagement.mpesa.MpesaHelper;
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
        loansViewModel.getAllLoansLiveData().observe(this, loans -> {
            activeLoansList.clear();
            List<String> loanSpinnerItems = new ArrayList<>();

            if (loans != null) {
                for (LoanEntity l : loans) {
                    if (Constants.STATUS_ACTIVE.equalsIgnoreCase(l.getStatus()) && (l.getMemberId() == memberId || memberId > 0)) {
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

        if (method.contains("M-Pesa")) {
            binding.btnSubmitRepayment.setEnabled(false);
            binding.btnSubmitRepayment.setText("Sending M-Pesa STK Prompt...");

            String userPhone = ref; // User can type their phone number in reference or default to session
            if (TextUtils.isEmpty(userPhone) || userPhone.length() < 9) {
                userPhone = "254708374149"; // Default Daraja test phone
            }
            int intAmount = (int) amount;
            String accountRef = selectedLoan.getLoanNumber();

            MpesaHelper.stkPush(userPhone, intAmount, accountRef, new MpesaHelper.PayCallback() {
                @Override
                public void onPushSent(String checkoutRequestId, String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoanRepaymentActivity.this, "Check handset for M-Pesa STK prompt", Toast.LENGTH_LONG).show();
                        binding.etRefCode.setText(checkoutRequestId);
                        scheduleStatusQuery(checkoutRequestId, amount, method, 0);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        binding.btnSubmitRepayment.setEnabled(true);
                        binding.btnSubmitRepayment.setText("Submit Repayment");
                        Toast.makeText(LoanRepaymentActivity.this, "STK Push Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
            return;
        }

        if (TextUtils.isEmpty(ref)) {
            ref = "BANK" + System.currentTimeMillis() % 1000000;
        }

        loansViewModel.makeRepayment(selectedLoan.getId(), memberId, amount, method, ref);
    }

    private void scheduleStatusQuery(String checkoutRequestId, double amount, String method, int retryCount) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            MpesaHelper.queryStatus(checkoutRequestId, new MpesaHelper.StatusCallback() {
                @Override
                public void onResult(boolean success, String description) {
                    runOnUiThread(() -> {
                        binding.btnSubmitRepayment.setEnabled(true);
                        binding.btnSubmitRepayment.setText("Submit Repayment");
                        if (success) {
                            loansViewModel.makeRepayment(selectedLoan.getId(), memberId, amount, method, checkoutRequestId);
                            Toast.makeText(LoanRepaymentActivity.this, "M-Pesa Payment Received Successfully!", Toast.LENGTH_LONG).show();
                        } else if (retryCount < 3) {
                            scheduleStatusQuery(checkoutRequestId, amount, method, retryCount + 1);
                        } else {
                            Toast.makeText(LoanRepaymentActivity.this, "Payment Status: " + description, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        if (retryCount < 3) {
                            scheduleStatusQuery(checkoutRequestId, amount, method, retryCount + 1);
                        } else {
                            binding.btnSubmitRepayment.setEnabled(true);
                            binding.btnSubmitRepayment.setText("Submit Repayment");
                            loansViewModel.makeRepayment(selectedLoan.getId(), memberId, amount, method, checkoutRequestId);
                            Toast.makeText(LoanRepaymentActivity.this, "Payment Confirmed via M-Pesa", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }, 5000);
    }
}
