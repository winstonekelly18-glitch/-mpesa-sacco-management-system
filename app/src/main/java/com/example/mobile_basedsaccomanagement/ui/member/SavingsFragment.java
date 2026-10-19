package com.example.mobile_basedsaccomanagement.ui.member;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.adapters.TransactionAdapter;
import com.example.mobile_basedsaccomanagement.data.entity.TransactionEntity;
import com.example.mobile_basedsaccomanagement.databinding.FragmentSavingsBinding;
import com.example.mobile_basedsaccomanagement.models.Transaction;
import com.example.mobile_basedsaccomanagement.util.Validators;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;
import com.example.mobile_basedsaccomanagement.viewmodel.SavingsViewModel;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SavingsFragment extends Fragment {

    private FragmentSavingsBinding binding;
    private SavingsViewModel viewModel;
    private SessionManager sessionManager;
    private long memberId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSavingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        viewModel = new ViewModelProvider(this).get(SavingsViewModel.class);

        binding.rvSavingsHistory.setLayoutManager(new LinearLayoutManager(requireContext()));

        memberId = sessionManager.getUserId();

        observeViewModel();

        binding.btnDeposit.setOnClickListener(v -> showDepositDialog());
        binding.btnWithdraw.setOnClickListener(v -> showWithdrawDialog());
    }

    private void observeViewModel() {
        viewModel.getSavingByMemberIdLiveData(memberId).observe(getViewLifecycleOwner(), saving -> {
            if (saving != null) {
                binding.tvSavingsTotal.setText("KES " + String.format(Locale.getDefault(), "%,.2f", saving.getBalance()));
                binding.tvTotalContributions.setText("KES " + String.format(Locale.getDefault(), "%,.2f", saving.getTotalContributions()));
                binding.tvLastDepositDate.setText(saving.getLastDepositDate() != null ? saving.getLastDepositDate() : "N/A");
            }
        });

        viewModel.getSavingsTransactionsLiveData(memberId).observe(getViewLifecycleOwner(), txEntities -> {
            List<Transaction> savingsTx = new ArrayList<>();
            List<Entry> chartEntries = new ArrayList<>();
            float cumulativeBal = 0f;
            int chartIndex = 0;

            if (txEntities != null) {
                for (TransactionEntity e : txEntities) {
                    if ("DEPOSIT".equalsIgnoreCase(e.getType()) || "WITHDRAWAL".equalsIgnoreCase(e.getType())) {
                        savingsTx.add(new Transaction(
                                e.getId(), e.getTransactionCode(), memberId, e.getType(), e.getAmount(), e.getDate(), e.getTime(), e.getDescription(), e.getStatus()
                        ));

                        if ("DEPOSIT".equalsIgnoreCase(e.getType())) {
                            cumulativeBal += (float) e.getAmount();
                        } else {
                            cumulativeBal -= (float) e.getAmount();
                        }
                        if (cumulativeBal < 0) cumulativeBal = 0f;
                        chartEntries.add(new Entry(chartIndex++, cumulativeBal));
                    }
                }
            }

            TransactionAdapter adapter = new TransactionAdapter(requireContext(), savingsTx);
            binding.rvSavingsHistory.setAdapter(adapter);

            setupSavingsChart(chartEntries);
        });

        viewModel.getActionResult().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSavingsChart(List<Entry> entries) {
        if (entries.isEmpty()) {
            entries.add(new Entry(0, 0f));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Savings Balance (KES)");
        dataSet.setColor(Color.parseColor("#059669")); // Emerald Green
        dataSet.setCircleColor(Color.parseColor("#1A365D")); // Navy
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#D1FAE5"));

        LineData lineData = new LineData(dataSet);
        binding.chartSavingsGrowth.setData(lineData);
        binding.chartSavingsGrowth.getDescription().setEnabled(false);
        binding.chartSavingsGrowth.invalidate(); // Refresh chart
    }

    private void showDepositDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_amount_input, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etAmount = dialogView.findViewById(R.id.etDialogAmount);
        EditText etDesc = dialogView.findViewById(R.id.etDialogDescription);
        Button btnSubmit = dialogView.findViewById(R.id.btnDialogSubmit);

        tvTitle.setText("Deposit Savings");
        btnSubmit.setText("Confirm Deposit");

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            String amtStr = etAmount.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (!Validators.isPositiveNumber(amtStr)) {
                etAmount.setError("Enter a valid deposit amount > 0");
                return;
            }

            double amount = Double.parseDouble(amtStr);
            if (TextUtils.isEmpty(desc)) desc = "Savings Contribution Deposit";

            viewModel.depositSavings(memberId, amount, desc);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showWithdrawDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_amount_input, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etAmount = dialogView.findViewById(R.id.etDialogAmount);
        EditText etDesc = dialogView.findViewById(R.id.etDialogDescription);
        Button btnSubmit = dialogView.findViewById(R.id.btnDialogSubmit);

        tvTitle.setText("Withdraw Savings");
        btnSubmit.setText("Confirm Withdrawal");

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            String amtStr = etAmount.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (!Validators.isPositiveNumber(amtStr)) {
                etAmount.setError("Enter a valid withdrawal amount > 0");
                return;
            }

            double amount = Double.parseDouble(amtStr);
            if (TextUtils.isEmpty(desc)) desc = "Savings Withdrawal";

            viewModel.withdrawSavings(memberId, amount, desc);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
