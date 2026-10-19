package com.example.mobile_basedsaccomanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etPassword, etRegisterPin;
    private Button btnRegister;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try {
            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            Toast.makeText(this, "Firebase Initialization Notice: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etRegisterPin = findViewById(R.id.etRegisterPin);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String pin = etRegisterPin.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || pin.length() != 4) {
            Toast.makeText(this, "Please fill all fields and ensure PIN is 4 digits", Toast.LENGTH_LONG).show();
            return;
        }

        if (auth == null || db == null) {
            Toast.makeText(this, "Firebase services unavailable. Please check Google Play Services on emulator.", Toast.LENGTH_LONG).show();
            return;
        }

        // 1. Create User with Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult == null || authResult.getUser() == null) {
                        Toast.makeText(RegisterActivity.this, "Authentication created null user instance.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String userId = authResult.getUser().getUid();

                    // 2. Hash the 4-digit PIN using your PinUtils class
                    String hashedPin = PinUtils.hashPin(pin);

                    // 3. Store user details and hashed PIN in Firestore
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("fullName", fullName);
                    userMap.put("email", email);
                    userMap.put("phoneNumber", phone);
                    userMap.put("accountBalance", 0.0);
                    userMap.put("transactionPin", hashedPin);
                    userMap.put("createdAt", com.google.firebase.Timestamp.now());

                    db.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                // Move to Pin Activity or Dashboard
                                startIntent();
                            })
                            .addOnFailureListener(e -> 
                                Toast.makeText(RegisterActivity.this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e -> 
                    Toast.makeText(RegisterActivity.this, "Auth Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void startIntent() {
        Intent intent = new Intent(RegisterActivity.this, PinActivity.class);
        startActivity(intent);
        finish();
    }
}
