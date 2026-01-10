package com.example.flightmanagement.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flightmanagement.BookingActivity;
import com.example.flightmanagement.R;
import com.example.flightmanagement.model.Flight;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {
    private List<Flight> flightList;
    private OnFlightClickListener listener;

    public interface OnFlightClickListener {
        void onFlightClick(Flight flight);
        void onBookClick(Flight flight);
    }

    public FlightAdapter(List<Flight> flightList, OnFlightClickListener listener) {
        this.flightList = flightList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flight, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        Flight flight = flightList.get(position);
        holder.bind(flight, listener);
    }

    @Override
    public int getItemCount() {
        return flightList.size();
    }

    public void updateFlights(List<Flight> newFlights) {
        this.flightList = newFlights;
        notifyDataSetChanged();
    }

    static class FlightViewHolder extends RecyclerView.ViewHolder {
        private TextView flightNumber;
        private TextView airline;
        private TextView origin;
        private TextView destination;
        private TextView departureTime;
        private TextView arrivalTime;
        private TextView price;
        private TextView availableSeats;
        private TextView status;
        private Button bookButton;

        public FlightViewHolder(@NonNull View itemView) {
            super(itemView);
            flightNumber = itemView.findViewById(R.id.flight_number);
            airline = itemView.findViewById(R.id.airline);
            origin = itemView.findViewById(R.id.origin);
            destination = itemView.findViewById(R.id.destination);
            departureTime = itemView.findViewById(R.id.departure_time);
            arrivalTime = itemView.findViewById(R.id.arrival_time);
            price = itemView.findViewById(R.id.price);
            availableSeats = itemView.findViewById(R.id.available_seats);
            status = itemView.findViewById(R.id.status);
            bookButton = itemView.findViewById(R.id.book_button);
        }

        public void bind(Flight flight, OnFlightClickListener listener) {
            flightNumber.setText(flight.getFlightNumber());
            airline.setText(flight.getAirline());
            origin.setText(flight.getOrigin());
            destination.setText(flight.getDestination());
            departureTime.setText(formatTime(flight.getDepartureTime()));
            arrivalTime.setText(formatTime(flight.getArrivalTime()));
            price.setText(String.format(Locale.US, "$%.2f", flight.getPrice()));
            availableSeats.setText(String.format(Locale.US, "%d seats available", flight.getAvailableSeats()));
            status.setText(capitalizeFirst(flight.getStatus()));
            status.setTextColor(getStatusColor(flight.getStatus()));

            itemView.setOnClickListener(v -> listener.onFlightClick(flight));
            bookButton.setOnClickListener(v -> listener.onBookClick(flight));
        }

        private String formatTime(String dateTime) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.US);
                Date date = inputFormat.parse(dateTime);
                return outputFormat.format(date);
            } catch (Exception e) {
                return dateTime; // Return original if parsing fails
            }
        }

        private String capitalizeFirst(String text) {
            if (text == null || text.isEmpty()) return "";
            return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        }

        private int getStatusColor(String status) {
            if (status == null) return 0xFF757575; // Gray for null status
            switch (status.toLowerCase()) {
                case "scheduled":
                    return 0xFF4CAF50; // Green
                case "boarding":
                    return 0xFFFF9800; // Orange
                case "departed":
                    return 0xFF2196F3; // Blue
                case "arrived":
                    return 0xFF9C27B0; // Purple
                case "cancelled":
                    return 0xFFF44336; // Red
                case "delayed":
                    return 0xFFFF5722; // Deep Orange
                default:
                    return 0xFF757575; // Gray
            }
        }
    }
}
