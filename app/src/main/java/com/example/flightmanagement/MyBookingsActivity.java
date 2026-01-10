package com.example.flightmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.flightmanagement.adapter.BookingAdapter;
import com.example.flightmanagement.api.ApiService;
import com.example.flightmanagement.api.RetrofitClient;
import com.example.flightmanagement.model.Booking;
import com.example.flightmanagement.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingsActivity extends AppCompatActivity implements BookingAdapter.OnBookingClickListener {

    private RecyclerView bookingsRecyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        // Initialize views
        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyTextView = findViewById(R.id.emptyTextView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to view bookings", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Setup RecyclerView
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingAdapter = new BookingAdapter(bookingList, this);
        bookingsRecyclerView.setAdapter(bookingAdapter);

        // Setup swipe to refresh
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> loadBookings());
        }

        // Load bookings
        loadBookings();
    }

    private void loadBookings() {
        showLoading(true);
        String token = sessionManager.getAuthorizationHeader();

        apiService.getMyBookings(token, null, null, null).enqueue(new Callback<ApiService.BookingListResponse>() {
            @Override
            public void onResponse(Call<ApiService.BookingListResponse> call, Response<ApiService.BookingListResponse> response) {
                showLoading(false);
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (response.isSuccessful() && response.body() != null) {
                    bookingList.clear();
                    bookingList.addAll(response.body().data.bookings);
                    bookingAdapter.notifyDataSetChanged();

                    // Show empty state if no bookings
                    if (bookingList.isEmpty()) {
                        showEmptyState(true);
                    } else {
                        showEmptyState(false);
                    }
                } else {
                    String errorMsg = "Failed to load bookings";
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
                    Toast.makeText(MyBookingsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.BookingListResponse> call, Throwable t) {
                showLoading(false);
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Toast.makeText(MyBookingsActivity.this, 
                    "Network error: " + t.getMessage(), 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (bookingsRecyclerView != null) {
            bookingsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showEmptyState(boolean show) {
        if (emptyTextView != null) {
            emptyTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (bookingsRecyclerView != null) {
            bookingsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onBookingClick(Booking booking) {
        // Show booking details dialog
        showBookingDetailsDialog(booking);
    }

    @Override
    public void onCancelBooking(Booking booking) {
        // Show confirmation dialog before canceling
        new AlertDialog.Builder(this)
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?\n\nBooking Reference: " + 
                           booking.getBookingReference())
                .setPositiveButton("Yes, Cancel", (dialog, which) -> cancelBooking(booking))
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelBooking(Booking booking) {
        String token = sessionManager.getAuthorizationHeader();
        
        apiService.cancelBooking(token, booking.getId()).enqueue(new Callback<ApiService.BookingResponse>() {
            @Override
            public void onResponse(Call<ApiService.BookingResponse> call, Response<ApiService.BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(MyBookingsActivity.this, 
                        "Booking cancelled successfully", 
                        Toast.LENGTH_SHORT).show();
                    // Reload bookings to reflect the change
                    loadBookings();
                } else {
                    String errorMsg = "Failed to cancel booking";
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
                    Toast.makeText(MyBookingsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.BookingResponse> call, Throwable t) {
                Toast.makeText(MyBookingsActivity.this, 
                    "Network error: " + t.getMessage(), 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showBookingDetailsDialog(Booking booking) {
        String details = "Booking Reference: " + booking.getBookingReference() + "\n\n" +
                "Flight: " + booking.getFlightNumber() + "\n" +
                "Airline: " + booking.getAirline() + "\n" +
                "Route: " + booking.getOrigin() + " → " + booking.getDestination() + "\n\n" +
                "Passenger: " + booking.getFirstName() + " " + booking.getLastName() + "\n" +
                "Seat: " + booking.getSeatNumber() + "\n" +
                "Class: " + capitalizeFirst(booking.getBookingClass()) + "\n\n" +
                "Status: " + capitalizeFirst(booking.getStatus()) + "\n" +
                "Price: $" + String.format("%.2f", booking.getPrice());

        new AlertDialog.Builder(this)
                .setTitle("Booking Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
