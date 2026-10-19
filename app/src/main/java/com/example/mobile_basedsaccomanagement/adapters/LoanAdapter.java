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
import com.example.mobile_basedsaccomanagement.models.Loan;

import java.util.List;
import java.util.Locale;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.ViewHolder> {

    private final Context context;
    private final List<Loan> loanList;
    private OnLoanClickListener listener;

    public interface OnLoanClickListener {
        void onLoanClick(Loan loan);
    }

    public LoanAdapter(Context context, List<Loan> loanList, OnLoanClickListener listener) {
        this.context = context;
        this.loanList = loanList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_loan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Loan loan = loanList.get(position);

        holder.tvLoanNo.setText(loan.getLoanNumber());
        holder.tvLoanType.setText(loan.getLoanType() + " (" + loan.getDurationMonths() + " mos)");
        holder.tvLoanAmount.setText("KES " + String.format(Locale.getDefault(), "%,.2f", loan.getRequestedAmount()));
        holder.tvLoanBalance.setText("KES " + String.format(Locale.getDefault(), "%,.2f", loan.getOutstandingBalance()));
        holder.tvLoanDates.setText("Issued: " + loan.getIssuedDate() + " | Repaid: KES " + String.format(Locale.getDefault(), "%,.2f", loan.getAmountRepaid()));

        String status = loan.getStatus();
        holder.tvLoanStatusBadge.setText(status);

        if ("ACTIVE".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status)) {
            holder.tvLoanStatusBadge.setTextColor(Color.parseColor("#059669")); // Green
        } else if ("PENDING".equalsIgnoreCase(status)) {
            holder.tvLoanStatusBadge.setTextColor(Color.parseColor("#D97706")); // Amber
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            holder.tvLoanStatusBadge.setTextColor(Color.parseColor("#DC2626")); // Red
        } else if ("PAID".equalsIgnoreCase(status)) {
            holder.tvLoanStatusBadge.setTextColor(Color.parseColor("#2563EB")); // Blue
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onLoanClick(loan);
        });
    }

    @Override
    public int getItemCount() {
        return loanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLoanNo, tvLoanType, tvLoanAmount, tvLoanBalance, tvLoanStatusBadge, tvLoanDates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLoanNo = itemView.findViewById(R.id.tvLoanNo);
            tvLoanType = itemView.findViewById(R.id.tvLoanType);
            tvLoanAmount = itemView.findViewById(R.id.tvLoanAmount);
            tvLoanBalance = itemView.findViewById(R.id.tvLoanBalance);
            tvLoanStatusBadge = itemView.findViewById(R.id.tvLoanStatusBadge);
            tvLoanDates = itemView.findViewById(R.id.tvLoanDates);
        }
    }
}
