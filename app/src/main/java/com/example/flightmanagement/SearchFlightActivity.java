package com.example.flightmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flightmanagement.adapter.FlightAdapter;
import com.example.flightmanagement.api.ApiService;
import com.example.flightmanagement.api.RetrofitClient;
import com.example.flightmanagement.model.Flight;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFlightActivity extends AppCompatActivity implements FlightAdapter.OnFlightClickListener {

    private EditText originEditText;
    private EditText destinationEditText;
    private EditText departureDateEditText;
    private Button searchButton;
    private RecyclerView flightsRecyclerView;

    private ApiService apiService;
    private FlightAdapter flightAdapter;
    private List<Flight> flightList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flight);

        originEditText = findViewById(R.id.originEditText);
        destinationEditText = findViewById(R.id.destinationEditText);
        departureDateEditText = findViewById(R.id.departureDateEditText);
        searchButton = findViewById(R.id.searchButton);
        flightsRecyclerView = findViewById(R.id.flightsRecyclerView);

        apiService = RetrofitClient.getApiService();

        flightsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        flightAdapter = new FlightAdapter(flightList, this);
        flightsRecyclerView.setAdapter(flightAdapter);

        // Setup date picker for departure date
        departureDateEditText.setFocusable(false);
        departureDateEditText.setClickable(true);
        departureDateEditText.setOnClickListener(v -> showDatePicker());

        searchButton.setOnClickListener(v -> searchFlights());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format date as YYYY-MM-DD
                    String formattedDate = String.format(Locale.US, "%04d-%02d-%02d", 
                            selectedYear, selectedMonth + 1, selectedDay);
                    departureDateEditText.setText(formattedDate);
                },
                year, month, day
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void searchFlights() {
        String origin = originEditText.getText().toString().trim();
        String destination = destinationEditText.getText().toString().trim();
        String departureDate = departureDateEditText.getText().toString().trim();

        // Validate inputs
        if (origin.isEmpty()) {
            originEditText.setError("Origin is required");
            originEditText.requestFocus();
            return;
        }

        if (destination.isEmpty()) {
            destinationEditText.setError("Destination is required");
            destinationEditText.requestFocus();
            return;
        }

        // Validate date format if provided (optional field)
        if (!departureDate.isEmpty() && !isValidDateFormat(departureDate)) {
            departureDateEditText.setError("Invalid date format. Use YYYY-MM-DD");
            departureDateEditText.requestFocus();
            return;
        }

        // Disable search button during search
        searchButton.setEnabled(false);
        searchButton.setText("Searching...");

        // Pass empty string instead of null for optional departure_date
        String dateParam = departureDate.isEmpty() ? "" : departureDate;

        apiService.searchFlights(origin, destination, dateParam, null).enqueue(new Callback<ApiService.FlightListResponse>() {
            @Override
            public void onResponse(Call<ApiService.FlightListResponse> call, Response<ApiService.FlightListResponse> response) {
                // Re-enable search button
                searchButton.setEnabled(true);
                searchButton.setText("Search Flights");

                if (response.isSuccessful() && response.body() != null) {
                    flightList.clear();
                    flightList.addAll(response.body().data.flights);
                    flightAdapter.notifyDataSetChanged();

                    if (flightList.isEmpty()) {
                        Toast.makeText(SearchFlightActivity.this, 
                            "No flights found for the selected route" + 
                            (departureDate.isEmpty() ? "" : " on " + departureDate), 
                            Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SearchFlightActivity.this, 
                            "Found " + flightList.size() + " flight(s)", 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Failed to search flights";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                            // Try to extract the message from JSON error
                            if (errorMsg.contains("\"message\":")) {
                                errorMsg = errorMsg.split("\"message\":\"")[1].split("\"")[0];
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(SearchFlightActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.FlightListResponse> call, Throwable t) {
                // Re-enable search button
                searchButton.setEnabled(true);
                searchButton.setText("Search Flights");

                String errorMsg = "Network error. Please check your connection.";
                if (t.getMessage() != null) {
                    errorMsg = t.getMessage();
                }
                Toast.makeText(SearchFlightActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Validate date format (YYYY-MM-DD)
     */
    private boolean isValidDateFormat(String date) {
        if (date.length() != 10) {
            return false;
        }
        
        // Check format YYYY-MM-DD
        if (date.charAt(4) != '-' || date.charAt(7) != '-') {
            return false;
        }

        try {
            String[] parts = date.split("-");
            if (parts.length != 3) return false;

            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            // Basic validation
            if (year < 2020 || year > 2100) return false;
            if (month < 1 || month > 12) return false;
            if (day < 1 || day > 31) return false;

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void onFlightClick(Flight flight) {
        // Can be used to show flight details in the future
    }

    @Override
    public void onBookClick(Flight flight) {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("flightId", flight.getId());
        startActivity(intent);
    }
}
