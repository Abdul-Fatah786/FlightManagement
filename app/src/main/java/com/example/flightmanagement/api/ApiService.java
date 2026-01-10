package com.example.flightmanagement.api;

import com.example.flightmanagement.model.Booking;
import com.example.flightmanagement.model.Flight;
import com.example.flightmanagement.model.Passenger;
import com.example.flightmanagement.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // Authentication Endpoints
    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("auth/me")
    Call<UserResponse> getCurrentUser(@Header("Authorization") String token);

    // Flight Endpoints
    @GET("flights")
    Call<FlightListResponse> getAllFlights(
            @Query("status") String status,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset
    );

    @GET("flights/search")
    Call<FlightListResponse> searchFlights(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("departure_date") String departureDate,
            @Query("airline") String airline
    );

    @GET("flights/{id}")
    Call<FlightResponse> getFlight(@Path("id") int flightId);

    @POST("flights")
    Call<FlightResponse> createFlight(
            @Header("Authorization") String token,
            @Body Flight flight
    );

    @PUT("flights/{id}")
    Call<FlightResponse> updateFlight(
            @Header("Authorization") String token,
            @Path("id") int flightId,
            @Body Flight flight
    );

    @DELETE("flights/{id}")
    Call<ApiResponse> deleteFlight(
            @Header("Authorization") String token,
            @Path("id") int flightId
    );

    // Booking Endpoints
    @POST("bookings")
    Call<BookingResponse> createBooking(
            @Header("Authorization") String token,
            @Body BookingRequest request
    );

    @GET("bookings")
    Call<BookingListResponse> getMyBookings(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset
    );

    @GET("bookings/{id}")
    Call<BookingResponse> getBooking(
            @Header("Authorization") String token,
            @Path("id") int bookingId
    );

    @GET("bookings/reference/{reference}")
    Call<BookingResponse> getBookingByReference(
            @Header("Authorization") String token,
            @Path("reference") String reference
    );

    @PATCH("bookings/{id}/cancel")
    Call<BookingResponse> cancelBooking(
            @Header("Authorization") String token,
            @Path("id") int bookingId
    );

    // Passenger Endpoints
    @POST("passengers")
    Call<PassengerResponse> createPassenger(
            @Header("Authorization") String token,
            @Body Passenger passenger
    );

    @GET("passengers")
    Call<PassengerListResponse> getMyPassengers(
            @Header("Authorization") String token
    );

    @GET("passengers/{id}")
    Call<PassengerResponse> getPassenger(
            @Header("Authorization") String token,
            @Path("id") int passengerId
    );

    @PUT("passengers/{id}")
    Call<PassengerResponse> updatePassenger(
            @Header("Authorization") String token,
            @Path("id") int passengerId,
            @Body Passenger passenger
    );

    @DELETE("passengers/{id}")
    Call<ApiResponse> deletePassenger(
            @Header("Authorization") String token,
            @Path("id") int passengerId
    );

    // User Profile Endpoints
    @GET("users/profile")
    Call<UserResponse> getProfile(@Header("Authorization") String token);

    @PUT("users/profile")
    Call<UserResponse> updateProfile(
            @Header("Authorization") String token,
            @Body UpdateProfileRequest request
    );

    // Admin Endpoints
    @GET("admin/users")
    Call<UserListResponse> getAllUsers(
            @Header("Authorization") String token,
            @Query("role") String role,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset
    );

    @GET("admin/bookings")
    Call<BookingListResponse> getAllBookings(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset
    );

    @GET("admin/passengers")
    Call<PassengerListResponse> getAllPassengers(
            @Header("Authorization") String token,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset
    );

    @GET("admin/flights/{id}/bookings")
    Call<BookingListResponse> getFlightBookings(
            @Header("Authorization") String token,
            @Path("id") int flightId
    );

    @DELETE("admin/users/{id}")
    Call<ApiResponse> deleteUser(
            @Header("Authorization") String token,
            @Path("id") int userId
    );

    // Request/Response Classes
    class RegisterRequest {
        public String email;
        public String password;
        public String name;
        public String phone;
        public String role;

        public RegisterRequest(String email, String password, String name, String phone) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.phone = phone;
            this.role = "passenger";
        }
    }

    class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    class BookingRequest {
        public int flight_id;
        public int passenger_id;
        public String seat_number;
        public String booking_class;

        public BookingRequest(int flightId, int passengerId, String seatNumber, String bookingClass) {
            this.flight_id = flightId;
            this.passenger_id = passengerId;
            this.seat_number = seatNumber;
            this.booking_class = bookingClass;
        }
    }

    class UpdateProfileRequest {
        public String name;
        public String phone;

        public UpdateProfileRequest(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }
    }

    // Response wrapper classes
    class ApiResponse {
        public String status;
        public String message;
    }

    class AuthResponse {
        public String status;
        public AuthData data;

        public class AuthData {
            public User user;
            public String token;
        }
    }

    class UserResponse {
        public String status;
        public UserData data;

        public class UserData {
            public User user;
        }
    }

    class UserListResponse {
        public String status;
        public int results;
        public UserListData data;

        public class UserListData {
            public List<User> users;
        }
    }

    class FlightResponse {
        public String status;
        public FlightData data;

        public class FlightData {
            public Flight flight;
        }
    }

    class FlightListResponse {
        public String status;
        public int results;
        public FlightListData data;

        public class FlightListData {
            public List<Flight> flights;
        }
    }

    class BookingResponse {
        public String status;
        public BookingData data;

        public class BookingData {
            public Booking booking;
        }
    }

    class BookingListResponse {
        public String status;
        public int results;
        public BookingListData data;

        public class BookingListData {
            public List<Booking> bookings;
        }
    }

    class PassengerResponse {
        public String status;
        public PassengerData data;

        public class PassengerData {
            public Passenger passenger;
        }
    }

    class PassengerListResponse {
        public String status;
        public int results;
        public PassengerListData data;

        public class PassengerListData {
            public List<Passenger> passengers;
        }
    }
}
