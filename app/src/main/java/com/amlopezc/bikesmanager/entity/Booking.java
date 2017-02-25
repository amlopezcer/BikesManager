package com.amlopezc.bikesmanager.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"bookaddress", "bookdate", "booktype", "entityid", "id", "md5", "username"})
public class Booking extends JSONBean {

    @JsonIgnore
    public static final String ENTITY_ID = "entity_booking"; //To identify server responses for this entity
    @JsonIgnore
    public static final int BOOKING_TYPE_BIKE = 1; //To identify the booking type
    @JsonIgnore
    public static final int BOOKING_TYPE_SLOTS = 2; //To identify the booking type
    @JsonIgnore
    public static final int MAX_BOOKING_TIME = 1800000; //30'

    @JsonProperty("id")
    private int mId;
    @JsonProperty("username")
    private String mUserName;
    @JsonProperty("bookaddress")
    private String mBookAddress;
    @JsonProperty("bookdate")
    private String mBookDate;
    @JsonProperty("booktype")
    private int mBookType;
    @JsonProperty("entityid")
    private String mEntityid; //Redundant but avoids issues with JSON serialization

    public Booking() {}

    public Booking(String mUserName, String mBookAddress, String mBookDate, int mBookType) {
        this.mId = -1; //Not representative, the server will assign an appropriate ID, but this one is needed for serialization
        this.mUserName = mUserName;
        this.mBookAddress = mBookAddress;
        this.mBookDate = mBookDate;
        this.mBookType = mBookType;
        this.mEntityid = ENTITY_ID;
        processHashMD5();
    }

    //<editor-fold desc="GET">
    @Override
    public int getServerId() {
        return mId;
    }

    public int getmId() {
        return mId;
    }

    public String getmUserName() {
        return mUserName;
    }

    public String getmBookAddress() {
        return mBookAddress;
    }

    public String getmBookDate() {
        return mBookDate;
    }

    public String getmEntityid() {return mEntityid; }

    public int getmBookType() {
        return mBookType;
    }
    //</editor-fold>

    //<editor-fold desc="SET">
    @Override
    public void setServerId(int serverId) {
        int oldValue = this.mId;
        this.mId = serverId;
        support.firePropertyChange("mId", oldValue, serverId);
    }

    public void setmId(int mId) {
        int oldValue = this.mId;
        this.mId = mId;
        support.firePropertyChange("mId", oldValue, mId);
    }

    public void setmUserName(String mUserName) {
        String oldValue = this.mUserName;
        this.mUserName = mUserName;
        support.firePropertyChange("mUserName", oldValue, mUserName);
    }

    public void setmBookAddress(String mBookAddress) {
        String oldValue = this.mBookAddress;
        this.mBookAddress = mBookAddress;
        support.firePropertyChange("mBookAddress", oldValue, mBookAddress);
    }

    public void setmBookDate(String mBookDate) {
        String oldValue = this.mBookDate;
        this.mBookDate = mBookDate;
        support.firePropertyChange("mBookDate", oldValue, mBookDate);
    }

    public void setmBookType(int mBookType) {
        int oldValue = this.mBookType;
        this.mBookType = mBookType;
        support.firePropertyChange("mBookType", oldValue, mBookType);
    }

    public void setmEntityid(String mEntityid) {
        String oldValue = this.mEntityid;
        this.mEntityid = mEntityid;
        support.firePropertyChange("mEntityid", oldValue, mEntityid);
    }

    //</editor-fold>

    //<editor-fold desc="EQUALS AND HASHCODE">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Booking booking = (Booking) o;

        if (mId != booking.mId) return false;
        if (mBookType != booking.mBookType) return false;
        if (!mUserName.equals(booking.mUserName)) return false;
        if (!mBookAddress.equals(booking.mBookAddress)) return false;
        return mBookDate.equals(booking.mBookDate);

    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + mUserName.hashCode();
        result = 31 * result + mBookAddress.hashCode();
        result = 31 * result + mBookDate.hashCode();
        result = 31 * result + mBookType;
        return result;
    }
    //</editor-fold>
}
