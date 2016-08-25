package com.amlopezc.bikesmanager.entity;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Monitor of bookings over the app to ensure consistency and appropriate updates
 */
public class BookingGlobalMonitor {

    private HashMap<String, Booking> mGlobalBookings;

    //SINGLETON basic implementation
    private static BookingGlobalMonitor mInstance = null;

    private BookingGlobalMonitor() {
        initMonitor();
    }

    public static BookingGlobalMonitor getInstance() {
        if (mInstance == null)
            mInstance = new BookingGlobalMonitor();

        return mInstance;
    }

    private void initMonitor() {
        mGlobalBookings = new HashMap<>();
    }

    public void addBooking(Booking booking) {
        String key = booking.getmUserName() + "_" + booking.getmBookType();
        mGlobalBookings.put(key, booking);
    }

    public void addBookingList(List<Booking> bookingList) {
        for(Booking booking : bookingList)
            addBooking(booking);
    }

    public void removeBooking(Booking booking) {
        String key = booking.getmUserName() + "_" + booking.getmBookType();
        mGlobalBookings.remove(key);
    }

    public void clearMonitor() {
        mGlobalBookings.clear();
    }

    public ArrayList<Booking> getTimedOutBookings() {
        Booking currentBooking;
        ArrayList<Booking> timedOutBookings = new ArrayList<>();

        for (HashMap.Entry<String, Booking> entry : mGlobalBookings.entrySet()) {
            currentBooking = entry.getValue();
            if(getRemainingBookingTime(currentBooking) <= 0) {
                timedOutBookings.add(currentBooking);
                removeBooking(currentBooking);
            }
        }

        Log.i("Global Booking Monitor", "Timed out bookings = " + timedOutBookings.size());
        return timedOutBookings;
    }

    private long getRemainingBookingTime(Booking booking){
        long now = System.currentTimeMillis();
        long bookingDate = booking.getLongDateStored(booking.getmBookDate());

        return Booking.MAX_BOOKING_TIME - (now - bookingDate);
    }

}
