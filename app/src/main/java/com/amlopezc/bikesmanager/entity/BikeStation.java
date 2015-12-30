package com.amlopezc.bikesmanager.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class BikeStation extends JSONBean implements Parcelable {

    //TODO: Repasar cómo quedará esto finalmente para generar la base de datos final en condiciones

    //General Data
    @JsonProperty
    private int mId;
    @JsonProperty
    private String mAddress;
    @JsonProperty
    private int mServerId;
    @JsonProperty
    private String mUser;
    @JsonProperty
    private Timestamp mTimeStamp;

    // Coordinates
    @JsonProperty
    private double mLatitude;
    @JsonProperty
    private double mLongitude;

    //Station numbers
    @JsonProperty
    private int mTotalBikes;
    @JsonProperty
    private int mAvailableBikes;
    @JsonProperty
    private int mBrokenBikes;
    @JsonProperty
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

    public BikeStation(double mLatitude, double mLongitude, int mId, String mAddress,
                       Timestamp mTimeStamp, String mUser, int mTotalBikes, int mAvailableBikes,
                       int mBrokenBikes, int mReservedBikes) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mId = mId;
        this.mAddress = mAddress;
        this.mTimeStamp = mTimeStamp;
        this.mUser = mUser;
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

    //<editor-fold desc="GET">
    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public int getmId() {
        return mId;
    }

    public String getmAddress() {
        return mAddress;
    }

    public int getmServerId() {
        return mServerId;
    }

    public String getmUser() {
        return mUser;
    }

    public Timestamp getmTimeStamp() {
        return mTimeStamp;
    }

    public int getmTotalBikes() {
        return mTotalBikes;
    }

    public int getmAvailableBikes() {
        return mAvailableBikes;
    }

    public int getmBrokenBikes() {
        return mBrokenBikes;
    }

    public int getmReservedBikes() {
        return mReservedBikes;
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

    public void setmUser(String mUser) {
        String oldValue = this.mUser;
        this.mUser = mUser;
        support.firePropertyChange("mUser", oldValue, mUser);
    }

    public void setmTimeStamp(Timestamp mTimeStamp) {
        Timestamp oldValue = this.mTimeStamp;
        this.mTimeStamp = mTimeStamp;
        support.firePropertyChange("mTimeStamp", oldValue, mTimeStamp);
    }

    public void setmLatitude(double mLatitude) {
        double oldValue = this.mLatitude;
        this.mLatitude = mLatitude;
        support.firePropertyChange("mLatitude", oldValue, mLatitude);
    }

    public void setmLongitude(double mLongitude) {
        double oldValue = this.mLongitude;
        this.mLongitude = mLongitude;
        support.firePropertyChange("mLongitude", oldValue, mLongitude);
    }

    public void setmTotalBikes(int mTotalBikes) {
        int oldValue = this.mTotalBikes;
        this.mTotalBikes = mTotalBikes;
        support.firePropertyChange("mTotalBikes", oldValue, mTotalBikes);
    }

    public void setmAvailableBikes(int mAvailableBikes) {
        int oldValue = this.mAvailableBikes;
        this.mAvailableBikes = mAvailableBikes;
        support.firePropertyChange("mAvailableBikes", oldValue, mAvailableBikes);
    }

    public void setmBrokenBikes(int mBrokenBikes) {
        int oldValue = this.mBrokenBikes;
        this.mBrokenBikes = mBrokenBikes;
        support.firePropertyChange("mBrokenBikes", oldValue, mBrokenBikes);
    }

    public void setmReservedBikes(int mReservedBikes) {
        int oldValue = this.mReservedBikes;
        this.mReservedBikes = mReservedBikes;
        support.firePropertyChange("mReservedBikes", oldValue, mReservedBikes);
    }
    //</editor-fold>

    //<editor-fold desc="EQUALS & HASHCODE">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BikeStation that = (BikeStation) o;

        if (mId != that.mId) return false;
        if (Double.compare(that.mLatitude, mLatitude) != 0) return false;
        if (Double.compare(that.mLongitude, mLongitude) != 0) return false;
        if (mTotalBikes != that.mTotalBikes) return false;
        if (mAvailableBikes != that.mAvailableBikes) return false;
        if (mBrokenBikes != that.mBrokenBikes) return false;
        if (mReservedBikes != that.mReservedBikes) return false;
        if (!mAddress.equals(that.mAddress)) return false;
        if (!mUser.equals(that.mUser)) return false;
        return !(mTimeStamp != null ? !mTimeStamp.equals(that.mTimeStamp) : that.mTimeStamp != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = mId;
        result = 31 * result + mAddress.hashCode();
        result = 31 * result + mUser.hashCode();
        result = 31 * result + (mTimeStamp != null ? mTimeStamp.hashCode() : 0);
        temp = Double.doubleToLongBits(mLatitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mLongitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + mTotalBikes;
        result = 31 * result + mAvailableBikes;
        result = 31 * result + mBrokenBikes;
        result = 31 * result + mReservedBikes;
        return result;
    }
    //</editor-fold>

    public String getAvailabilityMessage() {
        return String.format("%d/%d", getmAvailableBikes(), getmTotalBikes());
    }



}
