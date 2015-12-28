package com.amlopezc.bikesmanager.entity;


import android.os.Parcel;
import android.os.Parcelable;

public class BikeStation implements Parcelable {

    // Coordinates
    private double mLatitude;
    private double mLongitude;
    //General Data
    private int mId;
    private String mAddress;
    //Station numbers
    private int mTotalBikes;
    private int mAvailableBikes;
    private int mBrokenBikes;
    private int mReservedBikes;

    public BikeStation(double mLatitude, double mLongitude, int mId, String mAddress,
                       int mTotalBikes, int mAvailableBikes, int mBrokenBikes, int mReservedBikes) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mId = mId;
        this.mAddress = mAddress;
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
        this.mId = in.readInt();
        this.mAddress = in.readString();
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
        dest.writeInt(mId);
        dest.writeString(mAddress);
        dest.writeInt(mTotalBikes);
        dest.writeInt(mAvailableBikes);
        dest.writeInt(mBrokenBikes);
        dest.writeInt(mReservedBikes);
    }
    //</editor-fold>

    //<editor-fold desc="GETTERS AND SETTERS">
    public double getLatitude() { return mLatitude; }

    public double getLongitude() { return mLongitude; }

    public int getNumber() {
        return mId;
    }

    public void setNumber(int mNumber) {
        this.mId = mNumber;
    }

    public String getAddress() { return mAddress; }

    public int getTotalBikes() { return mTotalBikes; }

    public int getAvailableBikes() {
        return mAvailableBikes;
    }

    public void setAvailableBikes(int mAvailableBikes) {
        this.mAvailableBikes = mAvailableBikes;
    }

    public int getBrokenBikes() {
        return mBrokenBikes;
    }

    public void setBrokenBikes(int mBrokenBikes) {
        this.mBrokenBikes = mBrokenBikes;
    }

    public int getReservedBikes() {
        return mReservedBikes;
    }

    public void setReservedBikes(int mReservedBikes) {
        this.mReservedBikes = mReservedBikes;
    }
    //</editor-fold>

    public String getAvailabilityMessage() {
        return String.format("%d/%d", getAvailableBikes(), getTotalBikes());
    }



}
