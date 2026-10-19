package com.example.mobile_basedsaccomanagement.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobile_basedsaccomanagement.adapters.MemberAdapter;
import com.example.mobile_basedsaccomanagement.data.entity.MemberEntity;
import com.example.mobile_basedsaccomanagement.databinding.ActivityAdminMemberManagementBinding;
import com.example.mobile_basedsaccomanagement.models.User;
import com.example.mobile_basedsaccomanagement.util.Constants;
import com.example.mobile_basedsaccomanagement.viewmodel.AdminViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminMemberManagementActivity extends AppCompatActivity {

    private ActivityAdminMemberManagementBinding binding;
    private AdminViewModel adminViewModel;

    private final List<User> allMembers = new ArrayList<>();
    private final List<User> filteredMembers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMemberManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        binding.rvAdminMembers.setLayoutManager(new LinearLayoutManager(this));

        observeViewModel();

        binding.etSearchMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMembers();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void observeViewModel() {
        adminViewModel.getAllMembersLiveData().observe(this, memberEntities -> {
            allMembers.clear();
            if (memberEntities != null) {
                for (MemberEntity e : memberEntities) {
                    allMembers.add(new User(
                            e.getId(), e.getMemberNumber(), e.getFullName(), e.getNationalId(), e.getPhoneNumber(),
                            "", e.getGender(), e.getDateOfBirth(), e.getAddress(), "", Constants.ROLE_MEMBER, e.getStatus(), ""
                    ));
                }
            }
            filterMembers();
        });

        adminViewModel.getActionResult().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterMembers() {
        if (binding == null) return;
        String query = binding.etSearchMember.getText().toString().trim().toLowerCase();
        filteredMembers.clear();

        for (User u : allMembers) {
            if (query.isEmpty() ||
                    u.getFullName().toLowerCase().contains(query) ||
                    u.getMemberNumber().toLowerCase().contains(query) ||
                    u.getNationalId().contains(query)) {
                filteredMembers.add(u);
            }
        }

        MemberAdapter adapter = new MemberAdapter(this, filteredMembers, new MemberAdapter.OnMemberActionListener() {
            @Override
            public void onApprove(User member) {
                adminViewModel.updateMemberStatus(member.getId(), Constants.STATUS_APPROVED);
            }

            @Override
            public void onReject(User member) {
                adminViewModel.updateMemberStatus(member.getId(), Constants.STATUS_REJECTED);
            }
        });

        binding.rvAdminMembers.setAdapter(adapter);
    }
}
