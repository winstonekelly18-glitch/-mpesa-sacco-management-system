package com.example.mobile_basedsaccomanagement.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.databinding.ActivityMemberMainBinding;
import com.example.mobile_basedsaccomanagement.ui.member.MemberDashboardFragment;
import com.example.mobile_basedsaccomanagement.ui.member.ProfileFragment;
import com.example.mobile_basedsaccomanagement.ui.member.SavingsFragment;
import com.example.mobile_basedsaccomanagement.ui.member.SharesFragment;
import com.example.mobile_basedsaccomanagement.ui.member.TransactionsFragment;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;

public class MemberMainActivity extends AppCompatActivity {

    private ActivityMemberMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemberMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (savedInstanceState == null) {
            loadFragment(new MemberDashboardFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                loadFragment(new MemberDashboardFragment());
                return true;
            } else if (itemId == R.id.nav_savings) {
                loadFragment(new SavingsFragment());
                return true;
            } else if (itemId == R.id.nav_shares) {
                loadFragment(new SharesFragment());
                return true;
            } else if (itemId == R.id.nav_transactions) {
                loadFragment(new TransactionsFragment());
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
