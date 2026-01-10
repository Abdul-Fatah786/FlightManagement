package com.example.flightmanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "FlightManagementSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ROLE = "role";
    private static final String KEY_AUTH_TOKEN = "authToken";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(int userId, String email, String name, String phone, 
                                   String role, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get user ID
     */
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    /**
     * Get user email
     */
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    /**
     * Get user full name
     */
    public String getFullName() {
        return sharedPreferences.getString(KEY_NAME, "");
    }

    /**
     * Get user phone
     */
    public String getPhone() {
        return sharedPreferences.getString(KEY_PHONE, "");
    }

    /**
     * Get user role
     */
    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, "passenger");
    }

    /**
     * Get auth token
     */
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, "");
    }

    /**
     * Get authorization header
     */
    public String getAuthorizationHeader() {
        return "Bearer " + getAuthToken();
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(getRole());
    }

    /**
     * Check if user is passenger
     */
    public boolean isPassenger() {
        return "passenger".equalsIgnoreCase(getRole());
    }

    /**
     * Update user profile
     */
    public void updateProfile(String name, String phone) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PHONE, phone);
        editor.apply();
    }

    /**
     * Clear session and logout user
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    /**
     * Get all user data as a formatted string (for debugging)
     */
    public String getUserInfo() {
        return "UserID: " + getUserId() + "\n" +
               "Email: " + getEmail() + "\n" +
               "Name: " + getFullName() + "\n" +
               "Phone: " + getPhone() + "\n" +
               "Role: " + getRole();
    }
}
