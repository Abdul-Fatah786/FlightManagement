package com.example.flightmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flightmanagement.api.ApiService;
import com.example.flightmanagement.api.RetrofitClient;
import com.example.flightmanagement.model.Booking;
import com.example.flightmanagement.model.Passenger;
import com.example.flightmanagement.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private EditText seatNumberEditText;
    private RadioGroup bookingClassRadioGroup;
    private Spinner passengerSpinner;
    private TextView selectPassengerText;

    private ApiService apiService;
    private SessionManager sessionManager;
    private int flightId;
    private int selectedPassengerId = -1;
    private List<Passenger> passengerList = new ArrayList<>();

    // Activity result launcher for creating passenger
    private final ActivityResultLauncher<Intent> createPassengerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Passenger created successfully, reload the list
                    loadPassengers();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        seatNumberEditText = findViewById(R.id.seatNumberEditText);
        bookingClassRadioGroup = findViewById(R.id.bookingClassRadioGroup);
        passengerSpinner = findViewById(R.id.passengerSpinner);
        selectPassengerText = findViewById(R.id.selectPassengerText);
        Button confirmBookingButton = findViewById(R.id.confirmBookingButton);

        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "You must be logged in to book a flight", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Get flightId from the intent
        flightId = getIntent().getIntExtra("flightId", -1);
        
        if (flightId == -1) {
            Toast.makeText(this, "Invalid flight selection", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load user's passengers
        loadPassengers();

        confirmBookingButton.setOnClickListener(v -> createBooking());
    }

    private void loadPassengers() {
        String token = sessionManager.getAuthorizationHeader();
        
        apiService.getMyPassengers(token).enqueue(new Callback<ApiService.PassengerListResponse>() {
            @Override
            public void onResponse(Call<ApiService.PassengerListResponse> call, Response<ApiService.PassengerListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    passengerList = response.body().data.passengers;
                    
                    if (passengerList.isEmpty()) {
                        showNoPassengerDialog();
                    } else {
                        setupPassengerSpinner();
                    }
                } else {
                    Toast.makeText(BookingActivity.this, "Failed to load passengers", Toast.LENGTH_SHORT).show();
                    showNoPassengerDialog();
                }
            }

            @Override
            public void onFailure(Call<ApiService.PassengerListResponse> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "Error loading passengers: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showNoPassengerDialog();
            }
        });
    }

    private void setupPassengerSpinner() {
        List<String> passengerNames = new ArrayList<>();
        passengerNames.add("Select a passenger");
        
        for (Passenger passenger : passengerList) {
            passengerNames.add(passenger.getFullName() + " (" + passenger.getPassportNumber() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, passengerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        passengerSpinner.setAdapter(adapter);

        passengerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedPassengerId = passengerList.get(position - 1).getId();
                } else {
                    selectedPassengerId = -1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPassengerId = -1;
            }
        });
    }

    private void showNoPassengerDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Passenger Profile")
                .setMessage("You need to create a passenger profile before booking a flight. Would you like to create one now?")
                .setPositiveButton("Create Profile", (dialog, which) -> {
                    // Navigate to create passenger activity
                    Intent intent = new Intent(BookingActivity.this, CreatePassengerActivity.class);
                    createPassengerLauncher.launch(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void createBooking() {
        // Validate passenger selection
        if (selectedPassengerId == -1) {
            Toast.makeText(this, "Please select a passenger", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate seat number
        String seatNumber = seatNumberEditText.getText().toString().trim();
        if (seatNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a seat number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get booking class
        int selectedRadioButtonId = bookingClassRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Please select a booking class", Toast.LENGTH_SHORT).show();
            return;
        }
        
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String bookingClass = selectedRadioButton.getText().toString().toLowerCase();

        // Create booking request
        String token = sessionManager.getAuthorizationHeader();
        ApiService.BookingRequest request = new ApiService.BookingRequest(
                flightId, selectedPassengerId, seatNumber, bookingClass);

        apiService.createBooking(token, request).enqueue(new Callback<ApiService.BookingResponse>() {
            @Override
            public void onResponse(Call<ApiService.BookingResponse> call, Response<ApiService.BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Booking booking = response.body().data.booking;
                    showSuccessDialog(booking.getBookingReference());
                } else {
                    String errorMsg = "Failed to create booking";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(BookingActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.BookingResponse> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSuccessDialog(String bookingReference) {
        new AlertDialog.Builder(this)
                .setTitle("Booking Successful!")
                .setMessage("Your booking has been confirmed.\n\nBooking Reference: " + bookingReference + 
                           "\n\nYou can view your booking details in 'My Bookings'.")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}
