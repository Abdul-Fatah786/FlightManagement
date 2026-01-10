package com.example.flightmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flightmanagement.adapter.BookingAdapter;
import com.example.flightmanagement.model.Booking;
import com.example.flightmanagement.utils.SessionManager;
import com.example.flightmanagement.api.ApiService;
import com.example.flightmanagement.api.RetrofitClient;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class PassengerActivity extends AppCompatActivity implements BookingAdapter.OnBookingClickListener {
    private RecyclerView bookingsRecyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList = new ArrayList<>();

    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService();
        
        // Ensure user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupUI();
        setupQuickActions();
        setupBanners();
        setupMyBookings();
    }

    private void setupUI() {
        ImageView notificationIcon = findViewById(R.id.notification_icon);
        if (notificationIcon != null) {
            notificationIcon.setOnClickListener(v -> 
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show());
        }

        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ImageView menuIcon = findViewById(R.id.menu_icon);
        if (menuIcon != null && drawerLayout != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_logout) {
                    sessionManager.logoutUser();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                }
                if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });

            View headerView = navigationView.getHeaderView(0);
            if (headerView != null) {
                TextView navName = headerView.findViewById(R.id.nav_header_name);
                TextView navEmail = headerView.findViewById(R.id.nav_header_email);
                if (navName != null) navName.setText(sessionManager.getFullName());
                if (navEmail != null) navEmail.setText(sessionManager.getEmail());
            }
        }

        TextView passengerName = findViewById(R.id.passenger_name);
        if (passengerName != null) {
            passengerName.setText(sessionManager.getFullName());
        }

        TextInputEditText searchBox = findViewById(R.id.search_box);
        if (searchBox != null) {
            searchBox.setOnClickListener(v -> 
                startActivity(new Intent(this, SearchFlightActivity.class)));
        }
    }

    private void setupQuickActions() {
        View bookFlightAction = findViewById(R.id.action_book_flight);
        if (bookFlightAction != null) {
            bookFlightAction.setOnClickListener(v -> startActivity(new Intent(this, SearchFlightActivity.class)));
        }

        View myBookingsAction = findViewById(R.id.action_my_bookings);
        if (myBookingsAction != null) {
            myBookingsAction.setOnClickListener(v -> startActivity(new Intent(this, MyBookingsActivity.class)));
        }

        View checkInAction = findViewById(R.id.action_check_in);
        if (checkInAction != null) {
            checkInAction.setOnClickListener(v -> Toast.makeText(this, "Check-in", Toast.LENGTH_SHORT).show());
        }

        View flightStatusAction = findViewById(R.id.action_flight_status);
        if (flightStatusAction != null) {
            flightStatusAction.setOnClickListener(v -> Toast.makeText(this, "Flight Status", Toast.LENGTH_SHORT).show());
        }
    }

    private void setupBanners() {
        // Implementation for banners
    }

    private void setupMyBookings() {
        bookingsRecyclerView = findViewById(R.id.bookings_recycler_view);
        if (bookingsRecyclerView != null) {
            bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            bookingAdapter = new BookingAdapter(bookingList, this);
            bookingsRecyclerView.setAdapter(bookingAdapter);
            loadMyBookings();
        }
    }

    private void loadMyBookings() {
        // Mock data for testing
        bookingList.clear();
        Booking mock = new Booking();
        mock.setBookingReference("FL-MOCK-789");
        mock.setAirline("SkyHigh Air");
        mock.setFlightNumber("SH101");
        mock.setOrigin("Los Angeles");
        mock.setDestination("Tokyo");
        mock.setDepartureTime("2024-01-15T08:30:00");
        mock.setFirstName(sessionManager.getFullName());
        mock.setLastName("");
        mock.setSeatNumber("14K");
        mock.setBookingClass("Business");
        mock.setStatus("confirmed");
        bookingList.add(mock);
        
        if (bookingAdapter != null) {
            bookingAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBookingClick(Booking booking) {
        Toast.makeText(this, "Booking: " + booking.getBookingReference(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelBooking(Booking booking) {
        Toast.makeText(this, "Cancelling booking...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
