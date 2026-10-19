package com.example.mobile_basedsaccomanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PinActivity extends AppCompatActivity {

    private EditText etPin;
    private Button btnSubmitPin;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        etPin = findViewById(R.id.etPin);
        btnSubmitPin = findViewById(R.id.btnSubmitPin);

        try {
            db = FirebaseFirestore.getInstance();
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
        } catch (Exception e) {
            Toast.makeText(this, "Firebase initialization error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        btnSubmitPin.setOnClickListener(v -> {
            String enteredPin = etPin.getText().toString().trim();

            if (enteredPin.length() != 4) {
                Toast.makeText(PinActivity.this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyPinFromFirestore(enteredPin);
        });
    }

    private void verifyPinFromFirestore(String inputPin) {
        if (db == null) {
            Toast.makeText(PinActivity.this, "Firebase Firestore is not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(PinActivity.this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String storedHashedPin = documentSnapshot.getString("transactionPin");

                        if (storedHashedPin != null && PinUtils.verifyPin(inputPin, storedHashedPin)) {
                            Toast.makeText(PinActivity.this, "PIN Verified Successfully!", Toast.LENGTH_SHORT).show();

                            String role = documentSnapshot.getString("role");
                            String fullName = documentSnapshot.getString("fullName");
                            String email = documentSnapshot.getString("email");

                            com.example.mobile_basedsaccomanagement.utils.SessionManager sessionManager =
                                    new com.example.mobile_basedsaccomanagement.utils.SessionManager(PinActivity.this);
                            if (!sessionManager.isLoggedIn()) {
                                sessionManager.createLoginSession(
                                        1001,
                                        "SACCO-1001",
                                        fullName != null ? fullName : "SACCO Member",
                                        email != null ? email : "member@sacco.com",
                                        role != null ? role : "MEMBER"
                                );
                            }

                            Intent intent;
                            if ("ADMIN".equalsIgnoreCase(role)) {
                                intent = new Intent(PinActivity.this, com.example.mobile_basedsaccomanagement.activities.AdminDashboardActivity.class);
                            } else {
                                intent = new Intent(PinActivity.this, com.example.mobile_basedsaccomanagement.activities.MemberDashboardActivity.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(PinActivity.this, "Incorrect PIN!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PinActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(PinActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}