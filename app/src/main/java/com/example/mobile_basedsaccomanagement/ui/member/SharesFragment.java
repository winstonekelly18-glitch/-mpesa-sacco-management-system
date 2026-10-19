package com.example.mobile_basedsaccomanagement.ui.member;

import android.graphics.Color;
import android.os.Bundle;
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

import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.databinding.FragmentSharesBinding;
import com.example.mobile_basedsaccomanagement.util.Validators;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;
import com.example.mobile_basedsaccomanagement.viewmodel.SharesViewModel;

import java.util.Locale;

public class SharesFragment extends Fragment {

    private FragmentSharesBinding binding;
    private SharesViewModel viewModel;
    private SessionManager sessionManager;
    private long memberId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSharesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        viewModel = new ViewModelProvider(this).get(SharesViewModel.class);

        memberId = sessionManager.getUserId();

        observeViewModel();

        binding.btnBuyShares.setOnClickListener(v -> showBuySharesDialog());
    }

    private void observeViewModel() {
        viewModel.getShareByMemberIdLiveData(memberId).observe(getViewLifecycleOwner(), share -> {
            if (share != null) {
                binding.tvTotalShares.setText(share.getTotalShares() + " Shares");
                binding.tvShareValue.setText("KES " + String.format(Locale.getDefault(), "%,.2f", share.getShareValue()) + " / share");
                binding.tvTotalContribution.setText("KES " + String.format(Locale.getDefault(), "%,.2f", share.getTotalContribution()));

                String status = share.getStatus();
                binding.tvShareStatusBadge.setText(status);
                if ("COMPLIANT".equalsIgnoreCase(status)) {
                    binding.tvShareStatusBadge.setTextColor(Color.parseColor("#059669"));
                } else {
                    binding.tvShareStatusBadge.setTextColor(Color.parseColor("#DC2626"));
                }
            }
        });

        viewModel.getActionResult().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBuySharesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_amount_input, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etSharesCount = dialogView.findViewById(R.id.etDialogAmount);
        EditText etDesc = dialogView.findViewById(R.id.etDialogDescription);
        Button btnSubmit = dialogView.findViewById(R.id.btnDialogSubmit);

        tvTitle.setText("Purchase SACCO Shares");
        etSharesCount.setHint("Number of Shares (KES 100/share)");
        etDesc.setHint("Payment Reference Code");
        btnSubmit.setText("Confirm Purchase");

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            String countStr = etSharesCount.getText().toString().trim();

            if (!Validators.isPositiveNumber(countStr)) {
                etSharesCount.setError("Enter a valid integer number of shares > 0");
                return;
            }

            try {
                int count = Integer.parseInt(countStr);
                viewModel.buyShares(memberId, count);
                dialog.dismiss();
            } catch (NumberFormatException e) {
                etSharesCount.setError("Enter a valid integer");
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
