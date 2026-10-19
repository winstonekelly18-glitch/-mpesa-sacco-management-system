package com.example.mobile_basedsaccomanagement.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.models.Transaction;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final Context context;
    private final List<Transaction> transactionList;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction tx = transactionList.get(position);

        holder.tvTxDescription.setText(tx.getDescription());
        holder.tvTxDateTime.setText(tx.getDate() + " " + tx.getTime());
        holder.tvTxStatus.setText(tx.getStatus());

        String type = tx.getType();
        double amount = tx.getAmount();

        if ("DEPOSIT".equalsIgnoreCase(type) || "LOAN_DISBURSEMENT".equalsIgnoreCase(type) || "SHARE_CONTRIBUTION".equalsIgnoreCase(type)) {
            holder.tvTxAmount.setText("+ KES " + String.format(Locale.getDefault(), "%,.2f", amount));
            holder.tvTxAmount.setTextColor(Color.parseColor("#059669")); // Green
            holder.tvTxIcon.setText(type.contains("LOAN") ? "💸" : "💰");
        } else {
            holder.tvTxAmount.setText("- KES " + String.format(Locale.getDefault(), "%,.2f", amount));
            holder.tvTxAmount.setTextColor(Color.parseColor("#DC2626")); // Red
            holder.tvTxIcon.setText("💳");
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTxIcon, tvTxDescription, tvTxDateTime, tvTxAmount, tvTxStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTxIcon = itemView.findViewById(R.id.tvTxIcon);
            tvTxDescription = itemView.findViewById(R.id.tvTxDescription);
            tvTxDateTime = itemView.findViewById(R.id.tvTxDateTime);
            tvTxAmount = itemView.findViewById(R.id.tvTxAmount);
            tvTxStatus = itemView.findViewById(R.id.tvTxStatus);
        }
    }
}
