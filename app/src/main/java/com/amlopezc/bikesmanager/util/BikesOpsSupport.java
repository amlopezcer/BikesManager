package com.amlopezc.bikesmanager.util;


import com.amlopezc.bikesmanager.entity.BikeStation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Non-instantiable utility class which manages station operations
 */
public final class BikesOpsSupport {

    private static final int BASIC_FARE = 1;   // Basic fare, it will change depending on the availability

    public static final String OP_TAKE_BIKE = "take";          //Constant strings for server connection ->
    public static final String OP_LEAVE_BIKE = "leave";        // -> (distinguishes "PUT" method in the URL)

    // Suppress default constructor for non-instantiability
    private BikesOpsSupport() {}

    //Getting current fare for the station, depending on the availability
    public static float getCurrentFare(BikeStation bikeStation) {
        int availability = getStationAvailability(bikeStation);
        if(availability == 0)
            return BASIC_FARE;
        else if (availability < 50)
            return BASIC_FARE * 2;
        else
            return BASIC_FARE;
    }

    //Getting station availability
    public static int getStationAvailability(BikeStation bikeStation) {
        return (bikeStation.getmAvailableBikes()*100) / bikeStation.getmTotalBikes();
    }

    //Updating station status, will return null if the op can't be completed
    public static BikeStation updateBikeStation(String operation, BikeStation bikeStation) {
        boolean isOperationPossible = false; //The bike station status allows to perform the operation?

        switch (operation) {
            case OP_TAKE_BIKE:
                //Are there bikes to take?
                isOperationPossible = bikeStation.getmAvailableBikes() > 0;
                if(isOperationPossible)
                    bikeStation.setmAvailableBikes(bikeStation.getmAvailableBikes() - 1);
                break;
            case OP_LEAVE_BIKE:
                //Is there room to leave bikes?
                isOperationPossible = (bikeStation.getmAvailableBikes() + bikeStation.getmBrokenBikes() + bikeStation.getmReservedBikes()) < bikeStation.getmTotalBikes();
                if(isOperationPossible)
                    bikeStation.setmAvailableBikes(bikeStation.getmAvailableBikes() + 1);
                break;
        }

        //Format TimeStamp and return the instance
        if(isOperationPossible) {
            bikeStation.setmTimeStamp(getCurrentDateFormatted());
            bikeStation.setServerId(bikeStation.getmId());
            return bikeStation;
        }

        //Op not possible
        return null;
    }

    //Formatting date data to insert it correctly in the Database, format: yyyy-mm-ddThh:mm:ss+01:00
    private static String getCurrentDateFormatted() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        Calendar cal = Calendar.getInstance();
        StringBuilder builder = new StringBuilder(dateFormat.format(cal.getTime()));
        return builder.append("T").append(timeFormat.format(cal.getTime())).append("+01:00").toString();
    }
}
