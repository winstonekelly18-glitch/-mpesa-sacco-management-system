package com.example.mobile_basedsaccomanagement.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.data.entity.MemberEntity;
import com.example.mobile_basedsaccomanagement.databinding.ActivityProfileBinding;
import com.example.mobile_basedsaccomanagement.util.Validators;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;
import com.example.mobile_basedsaccomanagement.viewmodel.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private SessionManager sessionManager;
    private MemberEntity currentMember;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && currentMember != null) {
                    currentMember.setProfileImagePath(uri.toString());
                    profileViewModel.updateMemberProfile(currentMember);
                    Glide.with(this).load(uri).into(binding.ivProfileAvatar);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        long userId = sessionManager.getUserId();

        observeViewModel(userId);

        binding.btnUpdateProfile.setOnClickListener(v -> performProfileUpdate());
        binding.btnChangePassword.setOnClickListener(v -> performChangePassword());
        binding.btnChangePhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    }

    private void observeViewModel(long userId) {
        profileViewModel.getMemberByUserIdLiveData(userId).observe(this, member -> {
            if (member != null) {
                currentMember = member;
                binding.tvProfileName.setText(member.getFullName());
                binding.tvProfileMemberNo.setText("Member No: " + member.getMemberNumber());
                binding.tvAccountStatus.setText("ACCOUNT STATUS: " + member.getStatus());

                binding.etProfilePhone.setText(member.getPhoneNumber());
                binding.etProfileEmail.setText(sessionManager.getEmail());
                binding.etProfileAddress.setText(member.getAddress());

                if (member.getProfileImagePath() != null) {
                    Glide.with(this)
                            .load(Uri.parse(member.getProfileImagePath()))
                            .placeholder(R.mipmap.ic_launcher)
                            .into(binding.ivProfileAvatar);
                }
            }
        });

        profileViewModel.getActionMessage().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performProfileUpdate() {
        if (currentMember == null) return;

        String phone = binding.etProfilePhone.getText().toString().trim();
        String address = binding.etProfileAddress.getText().toString().trim();

        if (!Validators.isValidKenyanPhone(phone)) {
            binding.etProfilePhone.setError("Enter valid Kenyan Phone (07XXXXXXXX / 01XXXXXXXX / +254XXXXXXXXX)");
            return;
        }

        if (TextUtils.isEmpty(address)) {
            binding.etProfileAddress.setError("Address required");
            return;
        }

        currentMember.setPhoneNumber(phone);
        currentMember.setAddress(address);
        profileViewModel.updateMemberProfile(currentMember);
    }

    private void performChangePassword() {
        String currentPass = binding.etCurrentPassword.getText().toString().trim();
        String newPass = binding.etNewPassword.getText().toString().trim();
        String confirmNewPass = binding.etConfirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPass)) {
            binding.etCurrentPassword.setError("Current password is required");
            return;
        }

        if (!Validators.isStrongPassword(newPass)) {
            binding.etNewPassword.setError("Password must be 8+ chars with uppercase, lowercase & digit");
            return;
        }

        if (!newPass.equals(confirmNewPass)) {
            binding.etConfirmNewPassword.setError("Passwords do not match");
            return;
        }

        long userId = sessionManager.getUserId();
        profileViewModel.changePassword(userId, currentPass, newPass);
    }
}
