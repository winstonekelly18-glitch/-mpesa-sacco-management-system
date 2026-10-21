package com.example.mobile_basedsaccomanagement.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.adapters.TransactionAdapter;
import com.example.mobile_basedsaccomanagement.db.DatabaseHelper;
import com.example.mobile_basedsaccomanagement.models.Savings;
import com.example.mobile_basedsaccomanagement.models.Transaction;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;

import com.example.mobile_basedsaccomanagement.mpesa.MpesaHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SavingsActivity extends AppCompatActivity {

    private TextView tvSavingsTotal, tvTotalContributions, tvMonthlyContrib;
    private Button btnDeposit, btnWithdraw;
    private RecyclerView rvSavingsHistory;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        tvSavingsTotal = findViewById(R.id.tvSavingsTotal);
        tvTotalContributions = findViewById(R.id.tvTotalContributions);
        tvMonthlyContrib = findViewById(R.id.tvMonthlyContrib);

        btnDeposit = findViewById(R.id.btnDeposit);
        btnWithdraw = findViewById(R.id.btnWithdraw);

        rvSavingsHistory = findViewById(R.id.rvSavingsHistory);
        rvSavingsHistory.setLayoutManager(new LinearLayoutManager(this));

        btnDeposit.setOnClickListener(v -> showMpesaStkPushDialog());
        btnWithdraw.setOnClickListener(v -> showWithdrawDialog());

        loadSavingsData();
    }

    private void loadSavingsData() {
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            Savings savings = dbHelper.getSavingsByUserId(userId);
            List<Transaction> allTx = dbHelper.getTransactionsByUserId(userId);
            List<Transaction> savingsTx = new ArrayList<>();
            for (Transaction t : allTx) {
                if ("DEPOSIT".equalsIgnoreCase(t.getType()) || "WITHDRAWAL".equalsIgnoreCase(t.getType())) {
                    savingsTx.add(t);
                }
            }

            runOnUiThread(() -> {
                if (savings != null) {
                    tvSavingsTotal.setText("KES " + String.format(Locale.getDefault(), "%,.2f", savings.getBalance()));
                    tvTotalContributions.setText("KES " + String.format(Locale.getDefault(), "%,.2f", savings.getTotalContributions()));
                    tvMonthlyContrib.setText("KES " + String.format(Locale.getDefault(), "%,.2f", savings.getMonthlyContribution()));
                }

                TransactionAdapter adapter = new TransactionAdapter(SavingsActivity.this, savingsTx);
                rvSavingsHistory.setAdapter(adapter);
            });
        });
    }

    private void showMpesaStkPushDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_mpesa_pay, null);
        builder.setView(dialogView);

        EditText etPhone = dialogView.findViewById(R.id.etMpesaPhone);
        EditText etAmount = dialogView.findViewById(R.id.etMpesaAmount);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelMpesa);
        Button btnSendStk = dialogView.findViewById(R.id.btnSendStkPush);

        etPhone.setText("254712345678");

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSendStk.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (TextUtils.isEmpty(phone) || phone.length() < 10) {
                etPhone.setError("Enter valid M-Pesa Phone Number (2547...)");
                return;
            }

            if (TextUtils.isEmpty(amountStr)) {
                etAmount.setError("Enter deposit amount");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            btnSendStk.setEnabled(false);
            btnSendStk.setText("Sending Prompt...");

            MpesaHelper.stkPush(phone, (int) amount, "SAVINGS-" + userId, new MpesaHelper.PayCallback() {
                @Override
                public void onPushSent(String checkoutRequestId, String customerMessage) {
                    dbHelper.depositSavings(userId, amount, "M-Pesa STK Deposit (" + checkoutRequestId + ")");
                    Toast.makeText(SavingsActivity.this, customerMessage, Toast.LENGTH_LONG).show();
                    loadSavingsData();
                    dialog.dismiss();
                }

                @Override
                public void onError(String errorMessage) {
                    btnSendStk.setEnabled(true);
                    btnSendStk.setText("Send STK Push");
                    Toast.makeText(SavingsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }

    private void showDepositDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_amount_input, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etAmount = dialogView.findViewById(R.id.etDialogAmount);
        EditText etDesc = dialogView.findViewById(R.id.etDialogDescription);
        Button btnSubmit = dialogView.findViewById(R.id.btnDialogSubmit);

        tvTitle.setText("Deposit to SACCO Savings");
        btnSubmit.setText("Confirm Deposit");

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (TextUtils.isEmpty(amountStr)) {
                etAmount.setError("Enter deposit amount");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                etAmount.setError("Enter a valid positive amount");
                return;
            }

            if (TextUtils.isEmpty(desc)) desc = "Savings Deposit";

            boolean success = dbHelper.depositSavings(userId, amount, desc);
            if (success) {
                Toast.makeText(this, "Successfully deposited KES " + amount, Toast.LENGTH_SHORT).show();
                loadSavingsData();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Deposit failed!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showWithdrawDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_amount_input, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etAmount = dialogView.findViewById(R.id.etDialogAmount);
        EditText etDesc = dialogView.findViewById(R.id.etDialogDescription);
        Button btnSubmit = dialogView.findViewById(R.id.btnDialogSubmit);

        tvTitle.setText("Withdraw from Savings");
        btnSubmit.setText("Confirm Withdrawal");

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (TextUtils.isEmpty(amountStr)) {
                etAmount.setError("Enter withdrawal amount");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                etAmount.setError("Enter a valid positive amount");
                return;
            }

            if (TextUtils.isEmpty(desc)) desc = "Savings Withdrawal";

            boolean success = dbHelper.withdrawSavings(userId, amount, desc);
            if (success) {
                Toast.makeText(this, "Successfully withdrew KES " + amount, Toast.LENGTH_SHORT).show();
                loadSavingsData();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Withdrawal failed! Insufficient balance.", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }
}
