package com.amlopezc.bikesmanager.entity;


import android.os.Parcel;
import android.os.Parcelable;

public class BikeStation implements Parcelable {


    // Coordinates
    private double mLatitude;
    private double mLongitude;
    //Number and description (address basically)
    private int mNumber;
    private String mDescription;
    //Station numbers
    private int mTotalBikes;
    private int mAvailableBikes;
    private int mBrokenBikes;
    private int mReservedBikes;

    public BikeStation(double mLatitude, double mLongitude, int mNumber, String mDescription,
                       int mTotalBikes, int mAvailableBikes, int mBrokenBikes, int mReservedBikes) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mNumber = mNumber;
        this.mDescription = mDescription;
        this.mTotalBikes = mTotalBikes;
        this.mAvailableBikes = mAvailableBikes;
        this.mBrokenBikes = mBrokenBikes;
        this.mReservedBikes = mReservedBikes;
    }

    //<editor-fold desc="PARCELABLE INTERFACE SUPPORT">
    public static final Creator<BikeStation> CREATOR = new Creator<BikeStation>() {
        @Override
        public BikeStation createFromParcel(Parcel in) {
            return new BikeStation(in);
        }

        @Override
        public BikeStation[] newArray(int size) {
            return new BikeStation[size];
        }
    };

    public BikeStation(Parcel in) {
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
        this.mNumber = in.readInt();
        this.mDescription = in.readString();
        this.mTotalBikes = in.readInt();
        this.mAvailableBikes = in.readInt();
        this.mBrokenBikes = in.readInt();
        this.mReservedBikes = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeInt(mNumber);
        dest.writeString(mDescription);
        dest.writeInt(mTotalBikes);
        dest.writeInt(mAvailableBikes);
        dest.writeInt(mBrokenBikes);
        dest.writeInt(mReservedBikes);
    }
    //</editor-fold>

    //<editor-fold desc="GETTERS AND SETTERS">
    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public int getmNumber() {
        return mNumber;
    }

    public void setmNumber(int mNumber) {
        this.mNumber = mNumber;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public int getmTotalBikes() {
        return mTotalBikes;
    }

    public void setmTotalBikes(int mTotalBikes) {
        this.mTotalBikes = mTotalBikes;
    }

    public int getmAvailableBikes() {
        return mAvailableBikes;
    }

    public void setmAvailableBikes(int mAvailableBikes) {
        this.mAvailableBikes = mAvailableBikes;
    }

    public int getmBrokenBikes() {
        return mBrokenBikes;
    }

    public void setmBrokenBikes(int mBrokenBikes) {
        this.mBrokenBikes = mBrokenBikes;
    }

    public int getmReservedBikes() {
        return mReservedBikes;
    }

    public void setmReservedBikes(int mReservedBikes) {
        this.mReservedBikes = mReservedBikes;
    }
    //</editor-fold>

    public String getAvailabilityMessage() {
        return String.format("%d\\%d", getmAvailableBikes(), getmTotalBikes());
    }



}
