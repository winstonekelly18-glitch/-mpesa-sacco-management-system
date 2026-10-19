package com.example.mobile_basedsaccomanagement.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobile_basedsaccomanagement.databinding.ActivityAdminReportsBinding;
import com.example.mobile_basedsaccomanagement.viewmodel.AdminViewModel;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminReportsActivity extends AppCompatActivity {

    private ActivityAdminReportsBinding binding;
    private AdminViewModel adminViewModel;

    private double totalSavings = 0.0;
    private double activeLoans = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        observeViewModel();
    }

    private void observeViewModel() {
        adminViewModel.getTotalSavingsPoolLiveData().observe(this, savings -> {
            totalSavings = savings != null ? savings : 0.0;
            binding.tvRptTotalSavings.setText("KES " + String.format(Locale.getDefault(), "%,.2f", totalSavings));
            updateBarChart();
        });

        adminViewModel.getTotalActiveLoansAmountLiveData().observe(this, loans -> {
            activeLoans = loans != null ? loans : 0.0;
            binding.tvRptActiveLoans.setText("KES " + String.format(Locale.getDefault(), "%,.2f", activeLoans));
            updateBarChart();
        });

        adminViewModel.getPendingLoansCountLiveData().observe(this, pending -> {
            int count = pending != null ? pending : 0;
            binding.tvRptPendingLoans.setText(count + " Applications");
        });

        adminViewModel.getAllMembersLiveData().observe(this, members -> {
            int count = members != null ? members.size() : 0;
            binding.tvRptTotalMembers.setText(count + " Members");
        });

        adminViewModel.getAllTransactionsLiveData().observe(this, txList -> {
            int count = txList != null ? txList.size() : 0;
            binding.tvRptTotalTxCount.setText(count + " Transactions");
        });
    }

    private void updateBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, (float) totalSavings));
        entries.add(new BarEntry(1f, (float) activeLoans));

        BarDataSet dataSet = new BarDataSet(entries, "SACCO Assets Breakdown");
        dataSet.setColors(Color.parseColor("#059669"), Color.parseColor("#D97706"));
        dataSet.setValueTextSize(11f);

        BarData barData = new BarData(dataSet);
        binding.chartFinancialBar.setData(barData);

        String[] labels = {"Savings Pool", "Active Loans"};
        XAxis xAxis = binding.chartFinancialBar.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        binding.chartFinancialBar.getDescription().setEnabled(false);
        binding.chartFinancialBar.invalidate();
    }
}
