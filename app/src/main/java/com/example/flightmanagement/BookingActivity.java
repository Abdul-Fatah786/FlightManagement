package com.example.flightmanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flightmanagement.api.ApiService;
import com.example.flightmanagement.api.RetrofitClient;
import com.example.flightmanagement.model.Booking;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private EditText seatNumberEditText;
    private RadioGroup bookingClassRadioGroup;
    private Button confirmBookingButton;

    private ApiService apiService;
    private int flightId;
    private int passengerId; // Assuming you have a way to get the current passenger ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        seatNumberEditText = findViewById(R.id.seatNumberEditText);
        bookingClassRadioGroup = findViewById(R.id.bookingClassRadioGroup);
        confirmBookingButton = findViewById(R.id.confirmBookingButton);

        apiService = RetrofitClient.getApiService();

        // Get flightId from the intent
        flightId = getIntent().getIntExtra("flightId", -1);

        // TODO: Get the current passenger ID. This might be from shared preferences or another source.
        passengerId = 1; // Placeholder

        confirmBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBooking();
            }
        });
    }

    private void createBooking() {
        String seatNumber = seatNumberEditText.getText().toString().trim();
        if (seatNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a seat number", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedRadioButtonId = bookingClassRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String bookingClass = selectedRadioButton.getText().toString().toLowerCase();

        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService.BookingRequest request = new ApiService.BookingRequest(flightId, passengerId, seatNumber, bookingClass);

        apiService.createBooking("Bearer " + token, request).enqueue(new Callback<ApiService.BookingResponse>() {
            @Override
            public void onResponse(Call<ApiService.BookingResponse> call, Response<ApiService.BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Booking booking = response.body().data.booking;
                    Toast.makeText(BookingActivity.this, "Booking created successfully! Reference: " + booking.getBookingReference(), Toast.LENGTH_LONG).show();
                    finish(); // Close the activity
                } else {
                    Toast.makeText(BookingActivity.this, "Failed to create booking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.BookingResponse> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
