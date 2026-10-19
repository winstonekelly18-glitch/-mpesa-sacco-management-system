package com.example.mobile_basedsaccomanagement.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.models.User;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private final Context context;
    private final List<User> memberList;
    private OnMemberActionListener listener;

    public interface OnMemberActionListener {
        void onApprove(User member);
        void onReject(User member);
    }

    public MemberAdapter(Context context, List<User> memberList, OnMemberActionListener listener) {
        this.context = context;
        this.memberList = memberList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User member = memberList.get(position);

        holder.tvMemberName.setText(member.getFullName());
        holder.tvMemberNoAndId.setText("Member No: " + member.getMemberNumber() + " | ID: " + member.getNationalId());
        holder.tvMemberContact.setText("Email: " + member.getEmail() + " | Phone: " + member.getPhoneNumber());

        String status = member.getStatus();
        holder.tvMemberStatusBadge.setText(status);

        if ("APPROVED".equalsIgnoreCase(status)) {
            holder.tvMemberStatusBadge.setTextColor(Color.parseColor("#059669"));
        } else if ("PENDING".equalsIgnoreCase(status)) {
            holder.tvMemberStatusBadge.setTextColor(Color.parseColor("#D97706"));
        } else {
            holder.tvMemberStatusBadge.setTextColor(Color.parseColor("#DC2626"));
        }

        holder.btnApproveMember.setOnClickListener(v -> {
            if (listener != null) listener.onApprove(member);
        });

        holder.btnRejectMember.setOnClickListener(v -> {
            if (listener != null) listener.onReject(member);
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemberName, tvMemberNoAndId, tvMemberContact, tvMemberStatusBadge;
        Button btnApproveMember, btnRejectMember;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMemberNoAndId = itemView.findViewById(R.id.tvMemberNoAndId);
            tvMemberContact = itemView.findViewById(R.id.tvMemberContact);
            tvMemberStatusBadge = itemView.findViewById(R.id.tvMemberStatusBadge);
            btnApproveMember = itemView.findViewById(R.id.btnApproveMember);
            btnRejectMember = itemView.findViewById(R.id.btnRejectMember);
        }
    }
}
