package com.example.mobile_basedsaccomanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.RegisterActivity;
import com.example.mobile_basedsaccomanagement.databinding.ActivityLoginBinding;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;
import com.example.mobile_basedsaccomanagement.viewmodel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;
    private GoogleSignInClient mGoogleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null && account.getIdToken() != null) {
                            firebaseAuthWithGoogle(account.getIdToken());
                        }
                    } catch (ApiException e) {
                        Toast.makeText(this, "Google Sign-In Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        observeViewModel();

        // Quick Auto Fill Buttons for smooth evaluation
        binding.btnFillMember.setOnClickListener(v -> {
            binding.etLoginInput.setText("john@example.com");
            binding.etPassword.setText("User123!");
        });

        binding.btnFillAdmin.setOnClickListener(v -> {
            binding.etLoginInput.setText("admin@sacco.com");
            binding.etPassword.setText("Admin123!");
        });

        binding.btnLogin.setOnClickListener(v -> performLogin());

        binding.btnGoogleSignIn.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        binding.tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(LoginActivity.this, "Google Sign-In Successful! Enter Security PIN.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, com.example.mobile_basedsaccomanagement.PinActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(LoginActivity.this, "Google Auth Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void observeViewModel() {
        authViewModel.getLoginResult().observe(this, result -> {
            if (result.success) {
                if (result.user != null) {
                    String fullName = result.member != null ? result.member.getFullName() : "SACCO Admin";
                    String memberNo = result.member != null ? result.member.getMemberNumber() : "ADM-001";

                    sessionManager.createLoginSession(
                            result.user.getId(),
                            memberNo,
                            fullName,
                            result.user.getEmail(),
                            result.user.getRole()
                    );

                    Toast.makeText(this, "Password Verified! Enter 4-Digit Security PIN.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, com.example.mobile_basedsaccomanagement.PinActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void performLogin() {
        String input = binding.etLoginInput.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(input)) {
            binding.tilLoginInput.setError("Enter Email or Member Number");
            return;
        } else {
            binding.tilLoginInput.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("Enter Password");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        if (input.contains("@")) {
            try {
                com.google.firebase.auth.FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(input, password);
            } catch (Exception ignored) {}
        }

        authViewModel.login(input, password);
    }

    private void showForgotPasswordDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Password Recovery")
                .setMessage("For security compliance, password resets are processed by SACCO Administration. Please contact admin@sacco.com or visit SACCO HQ.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
