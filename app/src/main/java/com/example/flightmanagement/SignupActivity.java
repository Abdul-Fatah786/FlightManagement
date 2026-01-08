package com.example.flightmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flightmanagement.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {
    private TextInputEditText nameEditText, emailEditText, passwordEditText, phoneEditText;
    private Button signupButton;
    private TextView loginText;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sessionManager = new SessionManager(this);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        signupButton = findViewById(R.id.signupButton);
        loginText = findViewById(R.id.loginText);

        if (signupButton != null) {
            signupButton.setOnClickListener(v -> {
                String name = nameEditText != null ? nameEditText.getText().toString().trim() : "";
                String email = emailEditText != null ? emailEditText.getText().toString().trim() : "";
                String password = passwordEditText != null ? passwordEditText.getText().toString().trim() : "";
                String phone = phoneEditText != null ? phoneEditText.getText().toString().trim() : "";

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mock Signup
                sessionManager.createLoginSession(1, email, name, phone, "passenger", "mock_token");
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                
                Intent intent = new Intent(SignupActivity.this, PassengerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        if (loginText != null) {
            loginText.setOnClickListener(v -> {
                finish(); // Go back to Login
            });
        }
    }
}
