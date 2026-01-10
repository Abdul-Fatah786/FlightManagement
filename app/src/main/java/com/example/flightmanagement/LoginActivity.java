package com.example.flightmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flightmanagement.api.ApiService;
import com.example.flightmanagement.api.RetrofitClient;
import com.example.flightmanagement.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signupText;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService();
        
        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            if (sessionManager.isAdmin()) {
                navigateToAdminActivity();
            } else {
                navigateToPassengerActivity();
            }
            return;
        }
        
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);

        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                String email = emailEditText != null ? emailEditText.getText().toString().trim() : "";
                String password = passwordEditText != null ? passwordEditText.getText().toString().trim() : "";

                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required");
                    return;
                }

                performLogin(email, password);
            });
        }

        if (signupText != null) {
            signupText.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            });
        }
    }
    
    private void performLogin(String email, String password) {
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        
        ApiService.LoginRequest request = new ApiService.LoginRequest(email, password);
        Call<ApiService.AuthResponse> call = apiService.login(request);
        
        call.enqueue(new Callback<ApiService.AuthResponse>() {
            @Override
            public void onResponse(Call<ApiService.AuthResponse> call, Response<ApiService.AuthResponse> response) {
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.AuthResponse authResponse = response.body();
                    if (authResponse.data != null && authResponse.data.user != null) {
                        // Save session
                        sessionManager.createLoginSession(
                            authResponse.data.user.getUserId(),
                            authResponse.data.user.getEmail(),
                            authResponse.data.user.getName(),
                            authResponse.data.user.getPhone(),
                            authResponse.data.user.getRole(),
                            authResponse.data.token
                        );
                        
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        
                        // Navigate based on user role
                        if (authResponse.data.user.isAdmin()) {
                            navigateToAdminActivity();
                        } else {
                            navigateToPassengerActivity();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Login failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.AuthResponse> call, Throwable t) {
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                Log.e(TAG, "Login failed", t);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void navigateToPassengerActivity() {
        Intent intent = new Intent(LoginActivity.this, PassengerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void navigateToAdminActivity() {
        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
