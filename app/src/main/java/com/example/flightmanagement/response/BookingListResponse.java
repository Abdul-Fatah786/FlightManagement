package com.example.flightmanagement.response;

import com.example.flightmanagement.model.Booking;
import java.util.List;

public class BookingListResponse {
    private String status;
    private int results;
    private BookingData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getResults() {
        return results;
    }

    public void setResults(int results) {
        this.results = results;
    }

    public BookingData getData() {
        return data;
    }

    public void setData(BookingData data) {
        this.data = data;
    }

    public static class BookingData {
        private List<Booking> bookings;

        public List<Booking> getBookings() {
            return bookings;
        }

        public void setBookings(List<Booking> bookings) {
            this.bookings = bookings;
        }
    }
}
