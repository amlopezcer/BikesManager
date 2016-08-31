package com.amlopezc.bikesmanager.entity;


import com.amlopezc.bikesmanager.net.HttpConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.util.Locale;

@JsonPropertyOrder({"address", "availablebikes", "basicfare", "changetimestamp", "entityid", "id",
        "latitude", "longitude", "md5", "reservedbikes", "reservedmoorings", "totalmoorings"})
public class BikeStation extends JSONBean {

    @JsonIgnore
    public static final String ENTITY_ID = "entity_bikestation"; //To identify server responses for this entity

    @JsonProperty("id")
    private int mId;
    @JsonProperty("address")
    private String mAddress;
    @JsonProperty("totalmoorings")
    private int mTotalMoorings;
    @JsonProperty("reservedmoorings")
    private int mReservedMoorings;
    @JsonProperty("availablebikes")
    private int mAvailableBikes;
    @JsonProperty("reservedbikes")
    private int mReservedBikes;
    @JsonProperty("latitude")
    private float mLatitude;
    @JsonProperty("longitude")
    private float mLongitude;
    @JsonProperty("changetimestamp")
    private String mChangeTimestamp;
    @JsonProperty("basicfare")
    private float mBasicFare;
    @JsonProperty("entityid")
    private String mEntityId; //Redundant but avoids issues with JSON serialization

    public BikeStation() {}

    public BikeStation(int mId, String mAddress, int mTotalMoorings, int mReservedMoorings,
                       int mAvailableBikes, int mReservedBikes, float mLatitude, float mLongitude,
                       String mChangeTimestamp, float mBasicFare, String mEntityId) {
        this.mId = mId;
        this.mAddress = mAddress;
        this.mTotalMoorings = mTotalMoorings;
        this.mReservedMoorings = mReservedMoorings;
        this.mAvailableBikes = mAvailableBikes;
        this.mReservedBikes = mReservedBikes;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mChangeTimestamp = mChangeTimestamp;
        this.mBasicFare = mBasicFare;
        this.mEntityId = mEntityId;
        processHashMD5();
    }

    //<editor-fold desc="GET">
    public int getmId() { return mId; }

    public String getmAddress() { return mAddress; }

    public int getmTotalMoorings() { return mTotalMoorings; }

    public int getmReservedMoorings() { return mReservedMoorings; }

    public int getmAvailableBikes() { return mAvailableBikes; }

    public int getmReservedBikes() { return mReservedBikes; }

    public float getmLatitude() { return mLatitude; }

    public float getmLongitude() { return mLongitude; }

    public String getmChangeTimestamp() { return mChangeTimestamp; }

    public float getmBasicFare() { return mBasicFare; }

    public String getmEntityId() { return mEntityId; }

    @Override
    public int getServerId() {
        return mId;
    }
    //</editor-fold>

    //<editor-fold desc="SET">
    public void setmId(int mId) {
        int oldValue = this.mId;
        this.mId = mId;
        support.firePropertyChange("mId", oldValue, mId);
    }

    public void setmAddress(String mAddress) {
        String oldValue = this.mAddress;
        this.mAddress = mAddress;
        support.firePropertyChange("mAddress", oldValue, mAddress);
    }

    public void setmTotalMoorings(int mTotalMoorings) {
        int oldValue = this.mTotalMoorings;
        this.mTotalMoorings = mTotalMoorings;
        support.firePropertyChange("mTotalMoorings", oldValue, mTotalMoorings);
    }

    public void setmReservedMoorings(int mReservedMoorings) {
        int oldValue = this.mReservedMoorings;
        this.mReservedMoorings = mReservedMoorings;
        support.firePropertyChange("mReservedMoorings", oldValue, mReservedMoorings);
    }

    public void setmAvailableBikes(int mAvailableBikes) {
        int oldValue = this.mAvailableBikes;
        this.mAvailableBikes = mAvailableBikes;
        support.firePropertyChange("mAvailableBikes", oldValue, mAvailableBikes);
    }

    public void setmReservedBikes(int mReservedBikes) {
        int oldValue = this.mReservedBikes;
        this.mReservedBikes = mReservedBikes;
        support.firePropertyChange("mReservedBikes", oldValue, mReservedBikes);
    }

    public void setmLatitude(float mLatitude) {
        float oldValue = this.mLatitude;
        this.mLatitude = mLatitude;
        support.firePropertyChange("mLatitude", oldValue, mLatitude);
    }

    public void setmLongitude(float mLongitude) {
        float oldValue = this.mLongitude;
        this.mLongitude = mLongitude;
        support.firePropertyChange("mLongitude", oldValue, mLongitude);
    }

    public void setmChangeTimestamp(String mChangeTimestamp) {
        String oldValue = this.mChangeTimestamp;
        this.mChangeTimestamp = mChangeTimestamp;
        support.firePropertyChange("mChangeTimestamp", oldValue, mChangeTimestamp);
    }

    public void setmBasicFare(float mBasicFare) {
        float oldValue = this.mBasicFare;
        this.mBasicFare = mBasicFare;
        support.firePropertyChange("mBasicFare", oldValue, mBasicFare);
    }

    @Override
    public void setServerId(int serverId) {
        int oldValue = this.mId;
        this.mId = serverId;
        support.firePropertyChange("mId", oldValue, serverId);
    }
    //</editor-fold>

    //<editor-fold desc="EQUALS & HASHCODE">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BikeStation that = (BikeStation) o;

        if (mId != that.mId) return false;
        if (mTotalMoorings != that.mTotalMoorings) return false;
        if (mReservedMoorings != that.mReservedMoorings) return false;
        if (mAvailableBikes != that.mAvailableBikes) return false;
        if (mReservedBikes != that.mReservedBikes) return false;
        if (Float.compare(that.mLatitude, mLatitude) != 0) return false;
        if (Float.compare(that.mLongitude, mLongitude) != 0) return false;
        if (Float.compare(that.mBasicFare, mBasicFare) != 0) return false;
        if (!mAddress.equals(that.mAddress)) return false;
        if (!mChangeTimestamp.equals(that.mChangeTimestamp)) return false;
        return mEntityId.equals(that.mEntityId);

    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + mAddress.hashCode();
        result = 31 * result + mTotalMoorings;
        result = 31 * result + mReservedMoorings;
        result = 31 * result + mAvailableBikes;
        result = 31 * result + mReservedBikes;
        result = 31 * result + (mLatitude != +0.0f ? Float.floatToIntBits(mLatitude) : 0);
        result = 31 * result + (mLongitude != +0.0f ? Float.floatToIntBits(mLongitude) : 0);
        result = 31 * result + mChangeTimestamp.hashCode();
        result = 31 * result + (mBasicFare != +0.0f ? Float.floatToIntBits(mBasicFare) : 0);
        result = 31 * result + mEntityId.hashCode();
        return result;
    }
    //</editor-fold>


    public int getAvailableMoorings() {
        return getmTotalMoorings() - getmAvailableBikes() - getmReservedBikes() - getmReservedMoorings();
    }

    //Get current fare for the station, depending on the availability
    public float getCurrentFare() {
        int availability = getStationAvailability();
        float currentFare;

        if(availability == 0)
            currentFare = getmBasicFare();
        else if (availability < 50)
            currentFare =  getmBasicFare() * 2;
        else
            currentFare =  getmBasicFare();

        BigDecimal result = round(currentFare, 2); //2 decimals
        return result.floatValue();
    }

    private BigDecimal round(float fare, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(fare));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    //Get station availability
    public int getStationAvailability() {
        return (getmAvailableBikes()*100) / getmTotalMoorings();
    }

    //Update station status, will return null if the op can't be completed
    public BikeStation updateBikeStation(String operation, BikeUser bikeUser) {
        boolean isOperationPossible = false; //The bike station status allows to perform the operation?

        switch (operation) {
            case HttpConstants.PUT_TAKE_BIKE:
                //Are there bikes to take?
                isOperationPossible = getmAvailableBikes() > 0 ||
                        (getmAvailableBikes() == 0 && bikeUser.getmBookAddress().equals(getmAddress()));
                if(isOperationPossible)
                    takeBike(bikeUser);
                break;
            case HttpConstants.PUT_LEAVE_BIKE:
                //Is there room to leave bikes?
                isOperationPossible = getAvailableMoorings() > 0 ||
                        (getAvailableMoorings() == 0 && bikeUser.getmMooringsAddress().equals(getmAddress()));
                if(isOperationPossible)
                    leaveBike(bikeUser);
                break;
            case HttpConstants.PUT_BOOK_BIKE:
                //Are there bikes to book?
                isOperationPossible = getmAvailableBikes() > 0;
                if(isOperationPossible)
                    bookBike();
                break;
            case HttpConstants.PUT_BOOK_MOORINGS:
                //Are there moorings to book?
                isOperationPossible = getAvailableMoorings() > 0;
                if(isOperationPossible)
                    bookMoorings();
                break;
        }

        //Format TimeStamp and return the instance
        if(isOperationPossible) {
            setmChangeTimestamp(getCurrentDateFormatted());
            return this;
        }

        //Op not possible
        return null;
    }

    private void takeBike(BikeUser bikeUser) {
        if(bikeUser.ismBookTaken())
            cancelBikeBooking();

        mAvailableBikes--;
    }

    private void leaveBike(BikeUser bikeUser) {
        if(bikeUser.ismMooringsTaken())
            cancelMooringsBooking();

        mAvailableBikes++;
    }

    private void bookBike() {
        mAvailableBikes--;
        mReservedBikes++;
    }

    private void bookMoorings() {
        mReservedMoorings++;
    }

    public void cancelBikeBooking(){
        mAvailableBikes++;
        mReservedBikes--;
        mChangeTimestamp = getCurrentDateFormatted();
    }

    public void cancelMooringsBooking(){
        mReservedMoorings--;
        mChangeTimestamp = getCurrentDateFormatted();
    }

    //Map markers header (id - address)
    public String getStationHeader() {
        String headerTemplate = "%d - %s";
        return String.format(Locale.getDefault(), headerTemplate, getmId(), getmAddress());
    }
}