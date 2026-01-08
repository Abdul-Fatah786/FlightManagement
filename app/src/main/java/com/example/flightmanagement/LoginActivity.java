package com.example.flightmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flightmanagement.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton, signupButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);
        signupButton = findViewById(R.id.btn_signup);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            // Mock Login
            sessionManager.createLoginSession("1", "Guest User", email, "passenger");
            startActivity(new Intent(LoginActivity.this, PassengerActivity.class));
            finish();
        });

        signupButton.setOnClickListener(v -> {
            // Navigate to Signup if it exists
            Toast.makeText(this, "Sign up clicked", Toast.LENGTH_SHORT).show();
        });
    }
}
