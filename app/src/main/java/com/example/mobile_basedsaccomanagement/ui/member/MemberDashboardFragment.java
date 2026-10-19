package com.example.mobile_basedsaccomanagement.ui.member;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobile_basedsaccomanagement.activities.LoginActivity;
import com.example.mobile_basedsaccomanagement.activities.LoanApplicationActivity;
import com.example.mobile_basedsaccomanagement.activities.LoanRepaymentActivity;
import com.example.mobile_basedsaccomanagement.activities.NotificationsActivity;
import com.example.mobile_basedsaccomanagement.adapters.TransactionAdapter;
import com.example.mobile_basedsaccomanagement.data.entity.LoanEntity;
import com.example.mobile_basedsaccomanagement.data.entity.TransactionEntity;
import com.example.mobile_basedsaccomanagement.databinding.FragmentMemberDashboardBinding;
import com.example.mobile_basedsaccomanagement.models.Transaction;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;
import com.example.mobile_basedsaccomanagement.viewmodel.MemberDashboardViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MemberDashboardFragment extends Fragment {

    private FragmentMemberDashboardBinding binding;
    private MemberDashboardViewModel viewModel;
    private SessionManager sessionManager;
    private long memberId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMemberDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        viewModel = new ViewModelProvider(this).get(MemberDashboardViewModel.class);

        binding.rvRecentTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));

        long userId = sessionManager.getUserId();
        binding.tvWelcomeName.setText("Hello, " + sessionManager.getFullName());
        binding.tvMemberNumber.setText("Member No: " + sessionManager.getMemberNumber());

        observeViewModel(userId);

        binding.btnNotifications.setOnClickListener(v -> startActivity(new Intent(requireContext(), NotificationsActivity.class)));
        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());

        binding.btnApplyLoan.setOnClickListener(v -> startActivity(new Intent(requireContext(), LoanApplicationActivity.class)));
        binding.btnRepayLoan.setOnClickListener(v -> startActivity(new Intent(requireContext(), LoanRepaymentActivity.class)));
    }

    private void observeViewModel(long userId) {
        viewModel.getMemberByUserIdLiveData(userId).observe(getViewLifecycleOwner(), member -> {
            if (member != null) {
                memberId = member.getId();

                // Observe Savings Balance
                viewModel.getSavingByMemberIdLiveData(memberId).observe(getViewLifecycleOwner(), saving -> {
                    double savingsBal = saving != null ? saving.getBalance() : 0.0;
                    binding.tvSavingsBalance.setText("KES " + String.format(Locale.getDefault(), "%,.2f", savingsBal));

                    // Observe Active Loans
                    viewModel.getLoansByMemberIdLiveData(memberId).observe(getViewLifecycleOwner(), loans -> {
                        double activeBal = 0.0;
                        if (loans != null) {
                            for (LoanEntity l : loans) {
                                if ("ACTIVE".equalsIgnoreCase(l.getStatus())) {
                                    activeBal += l.getOutstandingBalance();
                                }
                            }
                        }
                        binding.tvLoanBalance.setText("KES " + String.format(Locale.getDefault(), "%,.2f", activeBal));

                        double availableLimit = (savingsBal * 3.0) - activeBal;
                        if (availableLimit < 0) availableLimit = 0.0;
                        binding.tvLoanLimit.setText("KES " + String.format(Locale.getDefault(), "%,.2f", availableLimit));
                    });
                });

                // Observe Recent Transactions
                viewModel.getRecentTransactionsLiveData(memberId).observe(getViewLifecycleOwner(), txEntities -> {
                    List<Transaction> list = new ArrayList<>();
                    if (txEntities != null) {
                        int limit = Math.min(txEntities.size(), 5);
                        for (int i = 0; i < limit; i++) {
                            TransactionEntity e = txEntities.get(i);
                            list.add(new Transaction(
                                    e.getId(), e.getTransactionCode(), memberId, e.getType(), e.getAmount(), e.getDate(), e.getTime(), e.getDescription(), e.getStatus()
                            ));
                        }
                    }
                    TransactionAdapter adapter = new TransactionAdapter(requireContext(), list);
                    binding.rvRecentTransactions.setAdapter(adapter);
                });
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logoutUser();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
