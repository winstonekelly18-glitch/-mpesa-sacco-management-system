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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.databinding.ActivityAdminProductsBinding;
import com.example.mobile_basedsaccomanagement.util.Validators;
import com.example.mobile_basedsaccomanagement.viewmodel.AdminViewModel;

public class AdminProductsActivity extends AppCompatActivity {

    private ActivityAdminProductsBinding binding;
    private AdminViewModel adminViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        binding.rvProductsList.setLayoutManager(new LinearLayoutManager(this));

        binding.btnAddProduct.setOnClickListener(v -> showAddProductDialog());

        adminViewModel.getActionResult().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_amount_input, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etName = dialogView.findViewById(R.id.etDialogAmount);
        EditText etRate = dialogView.findViewById(R.id.etDialogDescription);
        Button btnSubmit = dialogView.findViewById(R.id.btnDialogSubmit);

        tvTitle.setText("Add New SACCO Loan Product");
        etName.setHint("Product Name (e.g. Asset Finance)");
        etName.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        etRate.setHint("Annual Interest Rate % (e.g. 8.5)");
        etRate.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        btnSubmit.setText("Create Product");

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String rateStr = etRate.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                etName.setError("Product name required");
                return;
            }

            if (!Validators.isPositiveNumber(rateStr)) {
                etRate.setError("Enter valid positive interest rate %");
                return;
            }

            double rate = Double.parseDouble(rateStr);
            adminViewModel.addProduct(name, rate, 1000.0, 500000.0, 24, "FLAT");
            dialog.dismiss();
        });

        dialog.show();
    }
}
