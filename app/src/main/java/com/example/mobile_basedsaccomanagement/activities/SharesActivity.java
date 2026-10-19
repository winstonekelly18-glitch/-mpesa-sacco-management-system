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

import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.db.DatabaseHelper;
import com.example.mobile_basedsaccomanagement.models.ShareAccount;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;

import java.util.Locale;

public class SharesActivity extends AppCompatActivity {

    private TextView tvTotalShares, tvShareValue, tvTotalShareValue, tvShareStatusBadge;
    private Button btnBuyShares;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shares);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        tvTotalShares = findViewById(R.id.tvTotalShares);
        tvShareValue = findViewById(R.id.tvShareValue);
        tvTotalShareValue = findViewById(R.id.tvTotalShareValue);
        tvShareStatusBadge = findViewById(R.id.tvShareStatusBadge);
        btnBuyShares = findViewById(R.id.btnBuyShares);

        loadShareData();

        btnBuyShares.setOnClickListener(v -> showBuySharesDialog());
    }

    private void loadShareData() {
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            ShareAccount shares = dbHelper.getSharesByUserId(userId);
            runOnUiThread(() -> {
                if (shares != null) {
                    tvTotalShares.setText(shares.getTotalShares() + " Shares");
                    tvShareValue.setText("KES " + String.format(Locale.getDefault(), "%,.2f", shares.getShareValue()) + " / share");
                    tvTotalShareValue.setText("KES " + String.format(Locale.getDefault(), "%,.2f", shares.getTotalContribution()));
                    tvShareStatusBadge.setText("STATUS: " + shares.getStatus());
                }
            });
        });
    }

    private void showBuySharesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_amount_input, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etSharesCount = dialogView.findViewById(R.id.etDialogAmount);
        EditText etDesc = dialogView.findViewById(R.id.etDialogDescription);
        Button btnSubmit = dialogView.findViewById(R.id.btnDialogSubmit);

        tvTitle.setText("Purchase SACCO Shares");
        etSharesCount.setHint("Number of Shares (KES 100/share)");
        etDesc.setHint("Payment Reference");
        btnSubmit.setText("Confirm Purchase");

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            String countStr = etSharesCount.getText().toString().trim();

            if (TextUtils.isEmpty(countStr)) {
                etSharesCount.setError("Enter number of shares");
                return;
            }

            try {
                int count = Integer.parseInt(countStr);
                if (count <= 0) {
                    etSharesCount.setError("Enter a valid integer > 0");
                    return;
                }

                boolean success = dbHelper.buyShares(userId, count);
                if (success) {
                    Toast.makeText(this, "Successfully purchased " + count + " SACCO shares!", Toast.LENGTH_LONG).show();
                    loadShareData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Failed to purchase shares", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                etSharesCount.setError("Enter a valid integer number");
            }
        });

        dialog.show();
    }
}
