package com.amlopezc.bikesmanager.entity;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public abstract class JSONBean implements PropertyChangeListener {

    /**
     * Property to identify changes within the bean
     */
    @JsonProperty("md5")
    protected String md5;

    protected JSONBean() {
        support.addPropertyChangeListener(this);
    }

    //<editor-fold desc="PROPERTY CHANGE SUPPORT">
    @JsonIgnore
    protected PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
    //</editor-fold>

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        processHashMD5();
    }

    protected void processHashMD5() {
        HashFunction hashFunction = Hashing.md5();
        HashCode hashCode = hashFunction.hashInt(hashCode());
        md5 = hashCode.toString();
    }

    public String getMd5(){ return md5; }

    public abstract int getServerId();

    public abstract void setServerId(int serverId);

    //Format date data to insert it correctly in the Database, format: yyyy-mm-ddThh:mm:ss+02:00 (or +01:00), depending on the summer time
    protected String getCurrentDateFormatted() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);

        Calendar calendar = Calendar.getInstance();
        StringBuilder builder = new StringBuilder(dateFormat.format(calendar.getTime()));

        String currentDateString = calendar.getTime().toString();
        boolean winterTime = currentDateString.contains("CET");

        Log.d("PRUEBA HORARIA", "Mi String de hora= "+currentDateString+"; mi boolean winterTime= "+winterTime);

        //int month = Calendar.getInstance().get(Calendar.MONTH);

        String monthString; //Required because of JSON date formats (commented before)
        //if(month < 3 || month > 9) //0-index (3 = April, 9 = October), so before april or after october it's winter time
        if(winterTime)
            monthString = "+01:00"; //winter
        else
            monthString = "+02:00"; //summer

        return builder.append("T")
                .append(timeFormat.format(calendar.getTime()))
                .append(monthString).toString();

        //return Calendar.getInstance().getTime().toString();
    }

    //Obtain a long representation of a date
    protected long getLongDateStored(String dateStored) {

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        //Received: yyyy-mm-ddThh:mm:ss+01:00; wanted:yyyy-MM-dd HH:mm:ss
        String dateInString = dateStored.substring(0, 10) + " " + dateStored.substring(11, 19);
        try {
            Date date = formatter.parse(dateInString);
            return date.getTime();
        } catch (ParseException pe) {
            Log.e(getClass().getCanonicalName(), pe.getLocalizedMessage(), pe);
        }
        return 0;
    }

}
