package com.example.flightmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flightmanagement.R;
import com.example.flightmanagement.model.Booking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<Booking> bookingList;
    private OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
        void onCancelBooking(Booking booking);
    }

    public BookingAdapter(List<Booking> bookingList, OnBookingClickListener listener) {
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.bind(booking, listener);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void updateBookings(List<Booking> newBookings) {
        this.bookingList = newBookings;
        notifyDataSetChanged();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView bookingReference;
        private TextView flightNumber;
        private TextView airline;
        private TextView route;
        private TextView departureTime;
        private TextView passengerName;
        private TextView seatNumber;
        private TextView bookingClass;
        private TextView status;
        private View cancelButton;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingReference = itemView.findViewById(R.id.booking_reference);
            flightNumber = itemView.findViewById(R.id.flight_number);
            airline = itemView.findViewById(R.id.airline);
            route = itemView.findViewById(R.id.route);
            departureTime = itemView.findViewById(R.id.departure_time);
            passengerName = itemView.findViewById(R.id.passenger_name);
            seatNumber = itemView.findViewById(R.id.seat_number);
            bookingClass = itemView.findViewById(R.id.booking_class);
            status = itemView.findViewById(R.id.status);
            cancelButton = itemView.findViewById(R.id.cancel_button);
        }

        public void bind(Booking booking, OnBookingClickListener listener) {
            if (bookingReference != null) {
                bookingReference.setText(booking.getBookingReference());
            }
            
            if (flightNumber != null) {
                flightNumber.setText(booking.getFlightNumber());
            }
            
            if (airline != null) {
                airline.setText(booking.getAirline());
            }
            
            if (route != null) {
                route.setText(booking.getOrigin() + " → " + booking.getDestination());
            }
            
            if (departureTime != null) {
                departureTime.setText(formatDateTime(booking.getDepartureTime()));
            }
            
            if (passengerName != null) {
                passengerName.setText(booking.getFirstName() + " " + booking.getLastName());
            }
            
            if (seatNumber != null) {
                seatNumber.setText("Seat: " + booking.getSeatNumber());
            }
            
            if (bookingClass != null) {
                bookingClass.setText(capitalizeFirst(booking.getBookingClass()));
            }
            
            if (status != null) {
                status.setText(capitalizeFirst(booking.getStatus()));
                // Set status color based on booking status
                int statusColor = getStatusColor(booking.getStatus());
                status.setTextColor(statusColor);
            }

            // Handle cancel button
            if (cancelButton != null) {
                if ("confirmed".equalsIgnoreCase(booking.getStatus())) {
                    cancelButton.setVisibility(View.VISIBLE);
                    cancelButton.setOnClickListener(v -> listener.onCancelBooking(booking));
                } else {
                    cancelButton.setVisibility(View.GONE);
                }
            }

            itemView.setOnClickListener(v -> listener.onBookingClick(booking));
        }

        private String formatDateTime(String dateTime) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);
                Date date = inputFormat.parse(dateTime);
                return outputFormat.format(date);
            } catch (Exception e) {
                return dateTime;
            }
        }

        private String capitalizeFirst(String text) {
            if (text == null || text.isEmpty()) return text;
            return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        }

        private int getStatusColor(String status) {
            switch (status.toLowerCase()) {
                case "confirmed":
                    return 0xFF4CAF50; // Green
                case "cancelled":
                    return 0xFFF44336; // Red
                case "completed":
                    return 0xFF2196F3; // Blue
                default:
                    return 0xFF757575; // Gray
            }
        }
    }
}
