package com.example.mobile_basedsaccomanagement.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.adapters.TransactionAdapter;
import com.example.mobile_basedsaccomanagement.db.DatabaseHelper;
import com.example.mobile_basedsaccomanagement.models.Transaction;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    private EditText etSearchTx;
    private Spinner spTxFilter;
    private RecyclerView rvTransactionsList;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private long userId;

    private List<Transaction> allTransactions = new ArrayList<>();
    private List<Transaction> filteredTransactions = new ArrayList<>();
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        etSearchTx = findViewById(R.id.etSearchTx);
        spTxFilter = findViewById(R.id.spTxFilter);
        rvTransactionsList = findViewById(R.id.rvTransactionsList);

        rvTransactionsList.setLayoutManager(new LinearLayoutManager(this));

        String[] filters = {"All Types", "Deposits", "Withdrawals", "Loans", "Repayments", "Shares"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filters);
        spTxFilter.setAdapter(filterAdapter);

        loadTransactions();

        etSearchTx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilterAndSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        spTxFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilterAndSearch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadTransactions() {
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            allTransactions = dbHelper.getTransactionsByUserId(userId);
            runOnUiThread(this::applyFilterAndSearch);
        });
    }

    private void applyFilterAndSearch() {
        String query = etSearchTx.getText().toString().trim().toLowerCase();
        int filterPos = spTxFilter.getSelectedItemPosition();

        filteredTransactions.clear();
        for (Transaction t : allTransactions) {
            boolean matchesFilter = false;
            String type = t.getType().toUpperCase();

            switch (filterPos) {
                case 0: // All
                    matchesFilter = true;
                    break;
                case 1: // Deposits
                    matchesFilter = "DEPOSIT".equals(type);
                    break;
                case 2: // Withdrawals
                    matchesFilter = "WITHDRAWAL".equals(type);
                    break;
                case 3: // Loans
                    matchesFilter = "LOAN_DISBURSEMENT".equals(type);
                    break;
                case 4: // Repayments
                    matchesFilter = "LOAN_REPAYMENT".equals(type);
                    break;
                case 5: // Shares
                    matchesFilter = "SHARE_CONTRIBUTION".equals(type);
                    break;
            }

            boolean matchesSearch = query.isEmpty() ||
                    t.getDescription().toLowerCase().contains(query) ||
                    t.getTransactionId().toLowerCase().contains(query) ||
                    String.valueOf(t.getAmount()).contains(query);

            if (matchesFilter && matchesSearch) {
                filteredTransactions.add(t);
            }
        }

        adapter = new TransactionAdapter(this, filteredTransactions);
        rvTransactionsList.setAdapter(adapter);
    }
}
