package com.example.mobile_basedsaccomanagement.ui.member;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobile_basedsaccomanagement.adapters.TransactionAdapter;
import com.example.mobile_basedsaccomanagement.data.entity.TransactionEntity;
import com.example.mobile_basedsaccomanagement.databinding.FragmentTransactionsBinding;
import com.example.mobile_basedsaccomanagement.models.Transaction;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;
import com.example.mobile_basedsaccomanagement.viewmodel.TransactionsViewModel;

import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends Fragment {

    private FragmentTransactionsBinding binding;
    private TransactionsViewModel viewModel;
    private SessionManager sessionManager;
    private long memberId = -1;

    private List<Transaction> allTransactions = new ArrayList<>();
    private List<Transaction> filteredTransactions = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        viewModel = new ViewModelProvider(this).get(TransactionsViewModel.class);

        binding.rvTransactionsList.setLayoutManager(new LinearLayoutManager(requireContext()));

        memberId = sessionManager.getUserId();

        String[] filters = {"All Types", "Deposits", "Withdrawals", "Loans", "Repayments", "Shares"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filters);
        binding.spTxFilter.setAdapter(filterAdapter);

        observeViewModel();

        binding.etSearchTx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilterAndSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.spTxFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilterAndSearch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void observeViewModel() {
        viewModel.getTransactionsByMemberIdLiveData(memberId).observe(getViewLifecycleOwner(), txEntities -> {
            allTransactions.clear();
            if (txEntities != null) {
                for (TransactionEntity e : txEntities) {
                    allTransactions.add(new Transaction(
                            e.getId(), e.getTransactionCode(), memberId, e.getType(), e.getAmount(), e.getDate(), e.getTime(), e.getDescription(), e.getStatus()
                    ));
                }
            }
            applyFilterAndSearch();
        });
    }

    private void applyFilterAndSearch() {
        if (binding == null) return;
        String query = binding.etSearchTx.getText().toString().trim().toLowerCase();
        int filterPos = binding.spTxFilter.getSelectedItemPosition();

        filteredTransactions.clear();
        for (Transaction t : allTransactions) {
            boolean matchesFilter = false;
            String type = t.getType().toUpperCase();

            switch (filterPos) {
                case 0: matchesFilter = true; break;
                case 1: matchesFilter = "DEPOSIT".equals(type); break;
                case 2: matchesFilter = "WITHDRAWAL".equals(type); break;
                case 3: matchesFilter = "LOAN_DISBURSEMENT".equals(type); break;
                case 4: matchesFilter = "LOAN_REPAYMENT".equals(type); break;
                case 5: matchesFilter = "SHARE_CONTRIBUTION".equals(type); break;
            }

            boolean matchesSearch = query.isEmpty() ||
                    t.getDescription().toLowerCase().contains(query) ||
                    t.getTransactionId().toLowerCase().contains(query) ||
                    String.valueOf(t.getAmount()).contains(query);

            if (matchesFilter && matchesSearch) {
                filteredTransactions.add(t);
            }
        }

        TransactionAdapter adapter = new TransactionAdapter(requireContext(), filteredTransactions);
        binding.rvTransactionsList.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
