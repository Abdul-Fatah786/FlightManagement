package com.example.flightmanagement;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flightmanagement.api.ApiService;
import com.example.flightmanagement.api.RetrofitClient;
import com.example.flightmanagement.model.Passenger;
import com.example.flightmanagement.utils.SessionManager;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePassengerActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText dateOfBirthEditText;
    private EditText passportNumberEditText;
    private EditText nationalityEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private Button createPassengerButton;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_passenger);

        // Initialize views
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText);
        passportNumberEditText = findViewById(R.id.passportNumberEditText);
        nationalityEditText = findViewById(R.id.nationalityEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        createPassengerButton = findViewById(R.id.createPassengerButton);

        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(this);

        // Setup date picker for date of birth
        dateOfBirthEditText.setFocusable(false);
        dateOfBirthEditText.setClickable(true);
        dateOfBirthEditText.setOnClickListener(v -> showDatePicker());

        createPassengerButton.setOnClickListener(v -> createPassenger());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 18; // Default to 18 years ago
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format(Locale.US, "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    dateOfBirthEditText.setText(formattedDate);
                },
                year, month, day
        );

        // Set maximum date to today (can't be born in the future)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void createPassenger() {
        // Get input values
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String dateOfBirth = dateOfBirthEditText.getText().toString().trim();
        String passportNumber = passportNumberEditText.getText().toString().trim();
        String nationality = nationalityEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        // Validate inputs
        if (firstName.isEmpty()) {
            firstNameEditText.setError("First name is required");
            firstNameEditText.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            lastNameEditText.setError("Last name is required");
            lastNameEditText.requestFocus();
            return;
        }

        if (dateOfBirth.isEmpty()) {
            dateOfBirthEditText.setError("Date of birth is required");
            dateOfBirthEditText.requestFocus();
            return;
        }

        if (passportNumber.isEmpty()) {
            passportNumberEditText.setError("Passport number is required");
            passportNumberEditText.requestFocus();
            return;
        }

        if (nationality.isEmpty()) {
            nationalityEditText.setError("Nationality is required");
            nationalityEditText.requestFocus();
            return;
        }

        // Email and phone are optional but validate format if provided
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email format");
            emailEditText.requestFocus();
            return;
        }

        // Create passenger object
        Passenger passenger = new Passenger();
        passenger.setFirstName(firstName);
        passenger.setLastName(lastName);
        passenger.setDateOfBirth(dateOfBirth);
        passenger.setPassportNumber(passportNumber);
        passenger.setNationality(nationality);
        passenger.setEmail(email.isEmpty() ? null : email);
        passenger.setPhone(phone.isEmpty() ? null : phone);

        // Disable button during request
        createPassengerButton.setEnabled(false);
        createPassengerButton.setText("Creating...");

        String token = sessionManager.getAuthorizationHeader();

        apiService.createPassenger(token, passenger).enqueue(new Callback<ApiService.PassengerResponse>() {
            @Override
            public void onResponse(Call<ApiService.PassengerResponse> call, Response<ApiService.PassengerResponse> response) {
                createPassengerButton.setEnabled(true);
                createPassengerButton.setText("Create Passenger");

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreatePassengerActivity.this,
                            "Passenger profile created successfully!",
                            Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMsg = "Failed to create passenger profile";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                            if (errorMsg.contains("\"message\":")) {
                                errorMsg = errorMsg.split("\"message\":\"")[1].split("\"")[0];
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(CreatePassengerActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.PassengerResponse> call, Throwable t) {
                createPassengerButton.setEnabled(true);
                createPassengerButton.setText("Create Passenger");
                Toast.makeText(CreatePassengerActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
