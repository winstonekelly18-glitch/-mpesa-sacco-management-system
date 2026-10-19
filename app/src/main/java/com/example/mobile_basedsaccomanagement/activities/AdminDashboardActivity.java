package com.example.mobile_basedsaccomanagement.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobile_basedsaccomanagement.databinding.ActivityAdminDashboardBinding;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;
import com.example.mobile_basedsaccomanagement.viewmodel.AdminViewModel;

import java.util.Locale;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private AdminViewModel adminViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        observeViewModel();

        binding.btnAdminLogout.setOnClickListener(v -> showLogoutConfirmation());

        binding.btnAdminMembers.setOnClickListener(v -> startActivity(new Intent(this, AdminMemberManagementActivity.class)));
        binding.btnAdminLoanApps.setOnClickListener(v -> startActivity(new Intent(this, AdminLoanManagementActivity.class)));
        binding.btnAdminAnnouncements.setOnClickListener(v -> startActivity(new Intent(this, AdminAnnouncementsActivity.class)));
        binding.btnAdminReports.setOnClickListener(v -> startActivity(new Intent(this, AdminReportsActivity.class)));
    }

    private void observeViewModel() {
        adminViewModel.getTotalSavingsPoolLiveData().observe(this, savings -> {
            double val = savings != null ? savings : 0.0;
            binding.tvAdminTotalSavings.setText("KES " + String.format(Locale.getDefault(), "%,.2f", val));
        });

        adminViewModel.getTotalActiveLoansAmountLiveData().observe(this, loans -> {
            double val = loans != null ? loans : 0.0;
            binding.tvAdminActiveLoans.setText("KES " + String.format(Locale.getDefault(), "%,.2f", val));
        });

        adminViewModel.getAllMembersLiveData().observe(this, members -> {
            int count = members != null ? members.size() : 0;
            binding.tvAdminTotalMembers.setText(count + " Members");
        });

        adminViewModel.getPendingLoansCountLiveData().observe(this, pending -> {
            int count = pending != null ? pending : 0;
            binding.tvAdminPendingLoans.setText(count + " Pending");
        });
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Admin Logout")
                .setMessage("Are you sure you want to log out of Admin Portal?")
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
