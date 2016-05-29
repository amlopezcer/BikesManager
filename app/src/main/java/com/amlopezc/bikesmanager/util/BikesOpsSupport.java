package com.amlopezc.bikesmanager.util;


import com.amlopezc.bikesmanager.entity.BikeStation;
import com.amlopezc.bikesmanager.net.HttpConstants;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Non-instantiable utility class which manages station operations
 */
public final class BikesOpsSupport {

    private static final float BASIC_FARE = 1.00f;  // Basic fare, it will change depending on the availability

    //Suppress default constructor for non-instantiability
    private BikesOpsSupport() {}

    //Get current fare for the station, depending on the availability
    public static float getCurrentFare(BikeStation bikeStation) {
        int availability = getStationAvailability(bikeStation);
        float currentFare;

        if(availability == 0)
            currentFare = BASIC_FARE;
        else if (availability < 50)
            currentFare =  BASIC_FARE * 2;
        else
            currentFare =  BASIC_FARE;

        BigDecimal result = round(currentFare, 2); //2 decimals
        return result.floatValue();
    }

    private static BigDecimal round(float fare, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(fare));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    //Get station availability
    public static int getStationAvailability(BikeStation bikeStation) {
        return (bikeStation.getmAvailableBikes()*100) / bikeStation.getmTotalBikes();
    }

    //Update station status, will return null if the op can't be completed
    public static BikeStation updateBikeStation(String operation, BikeStation bikeStation) {
        boolean isOperationPossible = false; //The bike station status allows to perform the operation?

        switch (operation) {
            case HttpConstants.PUT_TAKE_BIKE:
                //Are there bikes to take?
                isOperationPossible = bikeStation.getmAvailableBikes() > 0;
                if(isOperationPossible)
                    bikeStation.setmAvailableBikes(bikeStation.getmAvailableBikes() - 1);
                break;
            case HttpConstants.PUT_LEAVE_BIKE:
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

    //Format date data to insert it correctly in the Database, format: yyyy-mm-ddThh:mm:ss+01:00
    private static String getCurrentDateFormatted() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        Calendar cal = Calendar.getInstance();
        StringBuilder builder = new StringBuilder(dateFormat.format(cal.getTime()));
        return builder.append("T")
                .append(timeFormat.format(cal.getTime()))
                .append("+01:00").toString();
    }
}
