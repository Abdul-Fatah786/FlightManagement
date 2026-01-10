package com.example.flightmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flightmanagement.adapter.BookingAdapter;
import com.example.flightmanagement.api.RetrofitClient;
import com.example.flightmanagement.api.ApiService;
import com.example.flightmanagement.model.Booking;
import com.example.flightmanagement.model.User;
import com.example.flightmanagement.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private TextView adminName;
    private TextView totalUsers;
    private TextView totalBookings;
    private TextView pendingBookings;
    private TextView completedBookings;
    private RecyclerView recentBookingsRecyclerView;
    private LinearLayout emptyState;

    private SessionManager sessionManager;
    private ApiService apiService;
    private List<Booking> bookingList;
    private BookingAdapter bookingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize SessionManager and ApiService
        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Check if user is admin
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Access denied. Admin only.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initializeViews();
        setupDrawer();
        setupRecyclerView();
        loadDashboardData();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);
        adminName = findViewById(R.id.admin_name);
        totalUsers = findViewById(R.id.total_users);
        totalBookings = findViewById(R.id.total_bookings);
        pendingBookings = findViewById(R.id.pending_bookings);
        completedBookings = findViewById(R.id.completed_bookings);
        recentBookingsRecyclerView = findViewById(R.id.recent_bookings_recyclerview);
        emptyState = findViewById(R.id.empty_state);

        // Set admin name
        String name = sessionManager.getFullName();
        if (name != null && !name.isEmpty()) {
            adminName.setText(name);
        }

        // Menu icon click
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        
        // Click on total users card - find parent view
        View totalUsersCard = (View) findViewById(R.id.total_users).getParent().getParent();
        totalUsersCard.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageUsersActivity.class));
        });
        
        // Click on view all bookings
        TextView viewAllBookings = findViewById(R.id.view_all_bookings);
        if (viewAllBookings != null) {
            viewAllBookings.setOnClickListener(v -> {
                startActivity(new Intent(this, MyBookingsActivity.class));
            });
        }
    }

    private void setupDrawer() {
        navigationView.setNavigationItemSelectedListener(this);

        // Set header info if header exists
        if (navigationView.getHeaderCount() > 0) {
            View headerView = navigationView.getHeaderView(0);
            // Header will be set up based on actual nav_header layout
        }
    }

    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(bookingList, new BookingAdapter.OnBookingClickListener() {
            @Override
            public void onBookingClick(Booking booking) {
                // View booking details
            }

            @Override
            public void onCancelBooking(Booking booking) {
                // Admin cannot cancel from here
            }
        });
        recentBookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recentBookingsRecyclerView.setAdapter(bookingAdapter);
    }

    private void loadDashboardData() {
        loadUsers();
        loadBookings();
    }

    private void loadUsers() {
        String token = "Bearer " + sessionManager.getAuthToken();
        Call<ApiService.UserListResponse> call = apiService.getAllUsers(token, null, null, null);

        call.enqueue(new Callback<ApiService.UserListResponse>() {
            @Override
            public void onResponse(Call<ApiService.UserListResponse> call, Response<ApiService.UserListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.UserListResponse userResponse = response.body();
                    if ("success".equals(userResponse.status)) {
                        List<User> users = userResponse.data.users;
                        totalUsers.setText(String.valueOf(users.size()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.UserListResponse> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Failed to load users: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBookings() {
        String token = "Bearer " + sessionManager.getAuthToken();
        Call<ApiService.BookingListResponse> call = apiService.getAllBookings(token, null, 10, 0);

        call.enqueue(new Callback<ApiService.BookingListResponse>() {
            @Override
            public void onResponse(Call<ApiService.BookingListResponse> call, Response<ApiService.BookingListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.BookingListResponse bookingResponse = response.body();
                    if ("success".equals(bookingResponse.status)) {
                        List<Booking> bookings = bookingResponse.data.bookings;
                        totalBookings.setText(String.valueOf(bookings.size()));

                        // Count pending and completed bookings
                        int pending = 0;
                        int completed = 0;
                        for (Booking booking : bookings) {
                            String status = booking.getStatus();
                            if ("pending".equalsIgnoreCase(status) || "confirmed".equalsIgnoreCase(status)) {
                                pending++;
                            } else if ("completed".equalsIgnoreCase(status)) {
                                completed++;
                            }
                        }
                        pendingBookings.setText(String.valueOf(pending));
                        completedBookings.setText(String.valueOf(completed));

                        // Show recent bookings
                        if (bookings.isEmpty()) {
                            recentBookingsRecyclerView.setVisibility(View.GONE);
                            emptyState.setVisibility(View.VISIBLE);
                        } else {
                            recentBookingsRecyclerView.setVisibility(View.VISIBLE);
                            emptyState.setVisibility(View.GONE);
                            bookingList.clear();
                            bookingList.addAll(bookings);
                            bookingAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.BookingListResponse> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Failed to load bookings: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Already on admin dashboard
        } else if (id == R.id.nav_logout) {
            sessionManager.logoutUser();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
