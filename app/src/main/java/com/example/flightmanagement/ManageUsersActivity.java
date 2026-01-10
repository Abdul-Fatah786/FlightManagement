package com.example.flightmanagement;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flightmanagement.adapter.UserAdapter;
import com.example.flightmanagement.api.RetrofitClient;
import com.example.flightmanagement.api.ApiService;
import com.example.flightmanagement.model.User;
import com.example.flightmanagement.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageUsersActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView usersRecyclerView;
    private ProgressBar progressBar;
    private LinearLayout emptyState;

    private SessionManager sessionManager;
    private ApiService apiService;
    private List<User> userList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Check if user is admin
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Access denied. Admin only.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupRecyclerView();
        loadUsers();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        usersRecyclerView = findViewById(R.id.users_recyclerview);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                // Show user details
                showUserDetails(user);
            }

            @Override
            public void onDeleteClick(User user) {
                // Confirm delete
                confirmDeleteUser(user);
            }
        });
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(userAdapter);
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getAuthToken();
        Call<ApiService.UserListResponse> call = apiService.getAllUsers(token, null, null, null);

        call.enqueue(new Callback<ApiService.UserListResponse>() {
            @Override
            public void onResponse(Call<ApiService.UserListResponse> call, Response<ApiService.UserListResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.UserListResponse userResponse = response.body();
                    if ("success".equals(userResponse.status)) {
                        List<User> users = userResponse.data.users;
                        if (users.isEmpty()) {
                            usersRecyclerView.setVisibility(View.GONE);
                            emptyState.setVisibility(View.VISIBLE);
                        } else {
                            usersRecyclerView.setVisibility(View.VISIBLE);
                            emptyState.setVisibility(View.GONE);
                            userList.clear();
                            userList.addAll(users);
                            userAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Toast.makeText(ManageUsersActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.UserListResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageUsersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserDetails(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Details");
        builder.setMessage(
                "Name: " + user.getName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Phone: " + (user.getPhone() != null ? user.getPhone() : "N/A") + "\n" +
                "Role: " + user.getRole()
        );
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void confirmDeleteUser(User user) {
        // Prevent deleting yourself
        if (user.getUserId() == sessionManager.getUserId()) {
            Toast.makeText(this, "You cannot delete your own account", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete User");
        builder.setMessage("Are you sure you want to delete " + user.getName() + "?");
        builder.setPositiveButton("Delete", (dialog, which) -> deleteUser(user));
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteUser(User user) {
        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getAuthToken();
        Call<ApiService.ApiResponse> call = apiService.deleteUser(token, user.getUserId());

        call.enqueue(new Callback<ApiService.ApiResponse>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse> call, Response<ApiService.ApiResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(ManageUsersActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    userList.remove(user);
                    userAdapter.notifyDataSetChanged();
                    
                    if (userList.isEmpty()) {
                        usersRecyclerView.setVisibility(View.GONE);
                        emptyState.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(ManageUsersActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageUsersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
