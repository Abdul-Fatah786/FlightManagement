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

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private TextInputEditText nameEditText, emailEditText, passwordEditText, phoneEditText;
    private Button signupButton;
    private TextView loginText;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService();
        
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

                if (TextUtils.isEmpty(name)) {
                    nameEditText.setError("Name is required");
                    return;
                }
                
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email is required");
                    return;
                }
                
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required");
                    return;
                }
                
                if (password.length() < 6) {
                    passwordEditText.setError("Password must be at least 6 characters");
                    return;
                }

                performSignup(name, email, password, phone);
            });
        }

        if (loginText != null) {
            loginText.setOnClickListener(v -> {
                finish(); // Go back to Login
            });
        }
    }
    
    private void performSignup(String name, String email, String password, String phone) {
        signupButton.setEnabled(false);
        signupButton.setText("Signing up...");
        
        ApiService.RegisterRequest request = new ApiService.RegisterRequest(email, password, name, phone);
        Call<ApiService.AuthResponse> call = apiService.register(request);
        
        call.enqueue(new Callback<ApiService.AuthResponse>() {
            @Override
            public void onResponse(Call<ApiService.AuthResponse> call, Response<ApiService.AuthResponse> response) {
                signupButton.setEnabled(true);
                signupButton.setText("Sign Up");
                
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
                        
                        Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        
                        Intent intent = new Intent(SignupActivity.this, PassengerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Registration failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                    Toast.makeText(SignupActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.AuthResponse> call, Throwable t) {
                signupButton.setEnabled(true);
                signupButton.setText("Sign Up");
                Log.e(TAG, "Signup failed", t);
                Toast.makeText(SignupActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
