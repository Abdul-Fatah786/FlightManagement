package com.example.flightmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flightmanagement.adapter.FlightAdapter;
import com.example.flightmanagement.api.ApiService;
import com.example.flightmanagement.api.RetrofitClient;
import com.example.flightmanagement.model.Flight;

import java.util.ArrayList;
import java.util.List;

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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFlights();
            }
        });
    }

    private void searchFlights() {
        String origin = originEditText.getText().toString().trim();
        String destination = destinationEditText.getText().toString().trim();
        String departureDate = departureDateEditText.getText().toString().trim();

        apiService.searchFlights(origin, destination, departureDate, null).enqueue(new Callback<ApiService.FlightListResponse>() {
            @Override
            public void onResponse(Call<ApiService.FlightListResponse> call, Response<ApiService.FlightListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    flightList.clear();
                    flightList.addAll(response.body().data.flights);
                    flightAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SearchFlightActivity.this, "Failed to search flights", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.FlightListResponse> call, Throwable t) {
                Toast.makeText(SearchFlightActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFlightClick(Flight flight) {
        // Can be used to show flight details in the future
    }

    @Override
    public void onBookClick(Flight flight) {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("flightId", flight.getFlightId());
        startActivity(intent);
    }
}
