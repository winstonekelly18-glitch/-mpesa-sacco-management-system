package com.example.mobile_basedsaccomanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.adapters.TransactionAdapter;
import com.example.mobile_basedsaccomanagement.db.DatabaseHelper;
import com.example.mobile_basedsaccomanagement.models.Loan;
import com.example.mobile_basedsaccomanagement.models.Savings;
import com.example.mobile_basedsaccomanagement.models.Transaction;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Locale;

public class MemberDashboardActivity extends AppCompatActivity {

    private TextView tvWelcomeName, tvMemberNumber, tvSavingsBalance, tvLoanBalance, tvLoanLimit, tvViewAllTx;
    private ImageButton btnNotifications, btnLogout;
    private MaterialCardView cardSavings, cardApplyLoan, cardRepayLoan, cardShares, cardTransactions, cardProfile;
    private RecyclerView rvRecentTransactions;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_dashboard);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userId = sessionManager.getUserId();

        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        tvMemberNumber = findViewById(R.id.tvMemberNumber);
        tvSavingsBalance = findViewById(R.id.tvSavingsBalance);
        tvLoanBalance = findViewById(R.id.tvLoanBalance);
        tvLoanLimit = findViewById(R.id.tvLoanLimit);
        tvViewAllTx = findViewById(R.id.tvViewAllTx);

        btnNotifications = findViewById(R.id.btnNotifications);
        btnLogout = findViewById(R.id.btnLogout);

        cardSavings = findViewById(R.id.cardSavings);
        cardApplyLoan = findViewById(R.id.cardApplyLoan);
        cardRepayLoan = findViewById(R.id.cardRepayLoan);
        cardShares = findViewById(R.id.cardShares);
        cardTransactions = findViewById(R.id.cardTransactions);
        cardProfile = findViewById(R.id.cardProfile);

        rvRecentTransactions = findViewById(R.id.rvRecentTransactions);
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(this));

        // Navigation click listeners
        btnNotifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());

        cardSavings.setOnClickListener(v -> startActivity(new Intent(this, SavingsActivity.class)));
        cardApplyLoan.setOnClickListener(v -> startActivity(new Intent(this, LoanApplicationActivity.class)));
        cardRepayLoan.setOnClickListener(v -> startActivity(new Intent(this, LoanRepaymentActivity.class)));
        cardShares.setOnClickListener(v -> startActivity(new Intent(this, SharesActivity.class)));
        cardTransactions.setOnClickListener(v -> startActivity(new Intent(this, TransactionsActivity.class)));
        cardProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        tvViewAllTx.setOnClickListener(v -> startActivity(new Intent(this, TransactionsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        tvWelcomeName.setText("Hello, " + sessionManager.getFullName());
        tvMemberNumber.setText("Member No: " + sessionManager.getMemberNumber());

        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            Savings savings = dbHelper.getSavingsByUserId(userId);
            double savingsBal = savings != null ? savings.getBalance() : 0.0;

            List<Loan> userLoans = dbHelper.getLoansByUserId(userId);
            double activeLoanBal = 0.0;
            for (Loan l : userLoans) {
                if ("ACTIVE".equalsIgnoreCase(l.getStatus())) {
                    activeLoanBal += l.getOutstandingBalance();
                }
            }

            double availableLimit = Math.max(0.0, (savingsBal * 3.0) - activeLoanBal);
            List<Transaction> transactions = dbHelper.getTransactionsByUserId(userId);
            List<Transaction> recentTx = transactions.size() > 5 ? transactions.subList(0, 5) : transactions;

            double finalSavingsBal = savingsBal;
            double finalActiveLoanBal = activeLoanBal;

            runOnUiThread(() -> {
                tvSavingsBalance.setText("KES " + String.format(Locale.getDefault(), "%,.2f", finalSavingsBal));
                tvLoanBalance.setText("KES " + String.format(Locale.getDefault(), "%,.2f", finalActiveLoanBal));
                tvLoanLimit.setText("KES " + String.format(Locale.getDefault(), "%,.2f", availableLimit));

                TransactionAdapter adapter = new TransactionAdapter(MemberDashboardActivity.this, recentTx);
                rvRecentTransactions.setAdapter(adapter);
            });
        });
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out of SACCO Mobile?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logoutUser();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
