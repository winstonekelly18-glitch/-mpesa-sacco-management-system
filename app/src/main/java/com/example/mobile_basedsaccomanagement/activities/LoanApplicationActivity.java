package com.example.mobile_basedsaccomanagement.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobile_basedsaccomanagement.data.entity.SaccoProductEntity;
import com.example.mobile_basedsaccomanagement.databinding.ActivityLoanApplicationBinding;
import com.example.mobile_basedsaccomanagement.util.LoanCalculator;
import com.example.mobile_basedsaccomanagement.util.Validators;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;
import com.example.mobile_basedsaccomanagement.viewmodel.LoansViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoanApplicationActivity extends AppCompatActivity {

    private ActivityLoanApplicationBinding binding;
    private LoansViewModel loansViewModel;
    private SessionManager sessionManager;
    private long memberId;

    private final List<SaccoProductEntity> productList = new ArrayList<>();
    private SaccoProductEntity selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoanApplicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        memberId = sessionManager.getUserId();

        loansViewModel = new ViewModelProvider(this).get(LoansViewModel.class);

        setupFrequenciesSpinner();
        observeViewModel();

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateSchedule();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.etRequestedAmount.addTextChangedListener(watcher);
        binding.etDurationMonths.addTextChangedListener(watcher);

        binding.spLoanType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < productList.size()) {
                    selectedProduct = productList.get(position);
                    calculateSchedule();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.btnSubmitLoanApp.setOnClickListener(v -> submitLoanApplication());
    }

    private void setupFrequenciesSpinner() {
        String[] frequencies = {"Monthly", "Weekly"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, frequencies);
        binding.spFrequency.setAdapter(adapter);
    }

    private void observeViewModel() {
        loansViewModel.getAllProductsLiveData().observe(this, products -> {
            productList.clear();
            List<String> productNames = new ArrayList<>();
            if (products != null) {
                productList.addAll(products);
                for (SaccoProductEntity p : products) {
                    productNames.add(p.getProductName() + " (" + p.getInterestRate() + "% p.a.)");
                }
            }

            if (productNames.isEmpty()) {
                productNames.add("Normal Development Loan (10% p.a.)");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, productNames);
            binding.spLoanType.setAdapter(adapter);

            if (!productList.isEmpty()) {
                selectedProduct = productList.get(0);
                calculateSchedule();
            }
        });

        loansViewModel.getActionResult().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                if (msg.contains("submitted successfully")) {
                    finish();
                }
            }
        });
    }

    private void calculateSchedule() {
        if (binding == null) return;
        String amtStr = binding.etRequestedAmount.getText().toString().trim();
        String durStr = binding.etDurationMonths.getText().toString().trim();

        double rate = selectedProduct != null ? selectedProduct.getInterestRate() : 10.0;
        binding.tvCalcInterestRate.setText(rate + "%");

        if (!Validators.isPositiveNumber(amtStr) || !Validators.isPositiveNumber(durStr)) {
            binding.tvCalcTotalRepayment.setText("KES 0.00");
            binding.tvCalcInstallment.setText("KES 0.00 / month");
            return;
        }

        try {
            double amount = Double.parseDouble(amtStr);
            int duration = Integer.parseInt(durStr);

            LoanCalculator.LoanCalculationResult calc = LoanCalculator.calculateFlatRateLoan(amount, rate, duration);

            binding.tvCalcTotalRepayment.setText("KES " + String.format(Locale.getDefault(), "%,.2f", calc.totalRepayment));
            binding.tvCalcInstallment.setText("KES " + String.format(Locale.getDefault(), "%,.2f", calc.monthlyInstallment) + " / month");
        } catch (NumberFormatException e) {
            binding.tvCalcTotalRepayment.setText("KES 0.00");
            binding.tvCalcInstallment.setText("KES 0.00 / month");
        }
    }

    private void submitLoanApplication() {
        String amtStr = binding.etRequestedAmount.getText().toString().trim();
        String durStr = binding.etDurationMonths.getText().toString().trim();
        String purpose = binding.etLoanPurpose.getText().toString().trim();
        String frequency = binding.spFrequency.getSelectedItem().toString();

        if (!Validators.isPositiveNumber(amtStr)) {
            binding.etRequestedAmount.setError("Enter valid requested amount > 0");
            return;
        }

        if (!Validators.isPositiveNumber(durStr)) {
            binding.etDurationMonths.setError("Enter valid duration in months > 0");
            return;
        }

        if (TextUtils.isEmpty(purpose)) {
            binding.etLoanPurpose.setError("Loan purpose required");
            return;
        }

        double amount = Double.parseDouble(amtStr);
        int duration = Integer.parseInt(durStr);

        if (selectedProduct != null) {
            if (amount < selectedProduct.getMinAmount() || amount > selectedProduct.getMaxAmount()) {
                Toast.makeText(this, "Amount must be between KES " + selectedProduct.getMinAmount() + " and KES " + selectedProduct.getMaxAmount(), Toast.LENGTH_LONG).show();
                return;
            }

            if (duration > selectedProduct.getMaxDurationMonths()) {
                Toast.makeText(this, "Maximum allowed duration for " + selectedProduct.getProductName() + " is " + selectedProduct.getMaxDurationMonths() + " months", Toast.LENGTH_LONG).show();
                return;
            }
        }

        long productId = selectedProduct != null ? selectedProduct.getId() : 1;
        loansViewModel.applyForLoan(memberId, productId, amount, duration, purpose, frequency);
    }
}
