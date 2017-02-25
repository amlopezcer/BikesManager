package com.amlopezc.bikesmanager.entity;


import com.amlopezc.bikesmanager.WelcomeActivityFragment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({"balance", "biketaken", "bookaddress", "bookdate", "booktaken", "email", "entityid",
        "fullname", "id", "md5", "password", "slotsaddress", "slotsdate", "slotstaken",
        "username"})
public class BikeUser extends JSONBean {

    @JsonIgnore
    public static final String ENTITY_ID = "entity_bikeuser"; //To identify server responses for this entity
    @JsonIgnore
    private final String ADDRESS_NONE_TEXT = "None"; //Initialization text for addresses variables

    @JsonProperty("id")
    private int mId;
    @JsonProperty("username")
    private String mUserName;
    @JsonProperty("password")
    private String mPassword;
    @JsonProperty("fullname")
    private String mFullName;
    @JsonProperty("email")
    private String mEmail;
    @JsonProperty("biketaken")
    private boolean mBikeTaken;
    @JsonProperty("booktaken") //Bikes bookings
    private boolean mBookTaken;
    @JsonProperty("slotstaken") //This filed refers only to a slots booking, you cannot take slots
    private boolean mSlotsTaken;
    @JsonProperty("bookaddress") //Bikes bookings
    private String mBookAddress;
    @JsonProperty("slotsaddress") //This filed refers only to a slots booking, you cannot take slots
    private String mSlotsAddress;
    @JsonProperty("bookdate") //Bikes bookings
    private String mBookDate;
    @JsonProperty("slotsdate") //This filed refers only to a slots booking, you cannot take slots
    private String mSlotsDate;
    @JsonProperty("balance")
    private float mBalance;
    @JsonProperty("entityid")
    private String mEntityid; //Redundant but avoids issues with JSON serialization

    //SINGLETON basic implementation
    @JsonIgnore
    private static BikeUser mInstance = null;

    private BikeUser() {}

    public static BikeUser getInstance() {
        if (mInstance == null)
            mInstance = new BikeUser();

        return mInstance;
    }

    //public BikeUser() {}


    //<editor-fold desc="GET">
    public int getmId() {
        return mId;
    }

    public String getmUserName() {
        return mUserName;
    }

    public String getmPassword() {
        return mPassword;
    }

    public String getmFullName() {
        return mFullName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public boolean ismBikeTaken() {
        return mBikeTaken;
    }

    public boolean ismBookTaken() {
        return mBookTaken;
    }

    public boolean ismSlotsTaken() {
        return mSlotsTaken;
    }

    public String getmBookAddress() {
        return mBookAddress;
    }

    public String getmSlotsAddress() {
        return mSlotsAddress;
    }

    public String getmBookDate() {
        return mBookDate;
    }

    public String getmSlotsDate() {
        return mSlotsDate;
    }

    public float getmBalance() {
        return mBalance;
    }

    public String getmEntityid() {return mEntityid; }

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

    public void setmUserName(String mUserName) {
        String oldValue = this.mUserName;
        this.mUserName = mUserName;
        support.firePropertyChange("mUserName", oldValue, mUserName);
    }

    public void setmPassword(String mPassword) {
        String oldValue = this.mPassword;
        this.mPassword = mPassword;
        support.firePropertyChange("mPassword", oldValue, mPassword);
    }

    public void setmFullName(String mFullName) {
        String oldValue = this.mFullName;
        this.mFullName = mFullName;
        support.firePropertyChange("mFullName", oldValue, mFullName);
    }

    public void setmEmail(String mEmail) {
        String oldValue = this.mEmail;
        this.mEmail = mEmail;
        support.firePropertyChange("mEmail", oldValue, mEmail);
    }

    public void setmBikeTaken(boolean mBikeTaken) {
        boolean oldValue = this.mBikeTaken;
        this.mBikeTaken = mBikeTaken;
        support.firePropertyChange("mBikeTaken", oldValue, mBikeTaken);
    }

    public void setmBookTaken(boolean mBookTaken) {
        boolean oldValue = this.mBookTaken;
        this.mBookTaken = mBookTaken;
        support.firePropertyChange("mBookTaken", oldValue, mBookTaken);
    }

    public void setmSlotsTaken(boolean mSlotsTaken) {
        boolean oldValue = this.mSlotsTaken;
        this.mSlotsTaken = mSlotsTaken;
        support.firePropertyChange("mSlotsTaken", oldValue, mSlotsTaken);
    }

    public void setmBookAddress(String mBookAddress) {
        String oldValue = this.mBookAddress;
        this.mBookAddress = mBookAddress;
        support.firePropertyChange("mBookAddress", oldValue, mBookAddress);
    }

    public void setmSlotsAddress(String mSlotsAddress) {
        String oldValue = this.mSlotsAddress;
        this.mSlotsAddress = mSlotsAddress;
        support.firePropertyChange("mSlotsAddress", oldValue, mSlotsAddress);
    }

    public void setmBookDate(String mBookDate) {
        String oldValue = this.mBookDate;
        this.mBookDate = mBookDate;
        support.firePropertyChange("mBookDate", oldValue, mBookDate);
    }

    public void setmSlotsDate(String mSlotsDate) {
        String oldValue = this.mSlotsDate;
        this.mSlotsDate = mSlotsDate;
        support.firePropertyChange("mSlotsDate", oldValue, mSlotsDate);
    }

    public void setmBalance(float mBalance) {
        float oldValue = this.mBalance;
        this.mBalance = mBalance;
        support.firePropertyChange("mBalance", oldValue, mBalance);
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

        BikeUser bikeUser = (BikeUser) o;

        if (mId != bikeUser.mId) return false;
        if (mBikeTaken != bikeUser.mBikeTaken) return false;
        if (mBookTaken != bikeUser.mBookTaken) return false;
        if (mSlotsTaken != bikeUser.mSlotsTaken) return false;
        if (Float.compare(bikeUser.mBalance, mBalance) != 0) return false;
        if (!mUserName.equals(bikeUser.mUserName)) return false;
        if (!mPassword.equals(bikeUser.mPassword)) return false;
        if (!mFullName.equals(bikeUser.mFullName)) return false;
        if (!mEmail.equals(bikeUser.mEmail)) return false;
        if (!mBookAddress.equals(bikeUser.mBookAddress)) return false;
        if (!mSlotsAddress.equals(bikeUser.mSlotsAddress)) return false;
        if (!mBookDate.equals(bikeUser.mBookDate)) return false;
        return mSlotsDate.equals(bikeUser.mSlotsDate);

    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + mUserName.hashCode();
        result = 31 * result + mPassword.hashCode();
        result = 31 * result + mFullName.hashCode();
        result = 31 * result + mEmail.hashCode();
        result = 31 * result + (mBikeTaken ? 1 : 0);
        result = 31 * result + (mBookTaken ? 1 : 0);
        result = 31 * result + (mSlotsTaken ? 1 : 0);
        result = 31 * result + mBookAddress.hashCode();
        result = 31 * result + mSlotsAddress.hashCode();
        result = 31 * result + mBookDate.hashCode();
        result = 31 * result + mSlotsDate.hashCode();
        result = 31 * result + (mBalance != +0.0f ? Float.floatToIntBits(mBalance) : 0);
        return result;
    }
    //</editor-fold>

    public void setNewUserData(String mUserName, String mPassword, String mFullName, String mEmail) {
        this.mId = -1; //Not representative, the server will assign an appropriate ID, but this one is needed for serialization
        this.mUserName = mUserName;
        this.mPassword = mPassword;
        this.mFullName = mFullName;
        this.mEmail = mEmail;
        //Some standard data for new users
        this.mBikeTaken = false;
        this.mBookTaken = false;
        this.mSlotsTaken = false;
        this.mBookAddress = ADDRESS_NONE_TEXT;
        this.mSlotsAddress = ADDRESS_NONE_TEXT;
        this.mBookDate = getCurrentDateFormatted();
        this.mSlotsDate = getCurrentDateFormatted();
        this.mBalance = WelcomeActivityFragment.NEW_USER_PRESENT; //Welcome present: 5.00€
        this.mEntityid = ENTITY_ID; //Redundant but avoids issues with JSON serialization
        processHashMD5();
    }

    public static BikeUser updateInstance(BikeUser bikeUser) {
        mInstance = bikeUser;
        return mInstance;
    }

    public void resetInstance () {
        mInstance = null;
    }

    public void payBike(float currentStationFare){
        float newBalance = getmBalance() - currentStationFare;
        if(newBalance <= 0)
            setmBalance(0); //In case "superuser" is activated (no restrictions)
        else
            setmBalance(newBalance);
    }

    public void takeBike() {
        if(ismBookTaken())
            cancelBookBike();

        setmBikeTaken(true);
    }

    public void leaveBike() {
        if(ismSlotsTaken())
            cancelBookSlots();

        setmBikeTaken(false);
    }

    public void bookBike(String bookAddress) {
        setmBookTaken(true);
        setmBookDate(getCurrentDateFormatted());
        setmBookAddress(bookAddress);
    }

    public void bookSlots(String bookAddress) {
        setmSlotsTaken(true);
        setmSlotsDate(getCurrentDateFormatted());
        setmSlotsAddress(bookAddress);
    }

    public void cancelBookBike() {
        setmBookTaken(false);
        setmBookDate(getCurrentDateFormatted());
        setmBookAddress(ADDRESS_NONE_TEXT);
    }

    public void cancelBookSlots() {
        setmSlotsTaken(false);
        setmSlotsDate(getCurrentDateFormatted());
        setmSlotsAddress(ADDRESS_NONE_TEXT);
    }

    public long getRemainingBookingTime(String date){
        long now = System.currentTimeMillis();
        long bookingDate = getLongDateStored(date);

        return Booking.MAX_BOOKING_TIME - (now - bookingDate);
    }

    public boolean isBookingTimedOut() {
        return isBikeBookingTimedOut() || isSlotsBookingTimedOut();
    }

    public boolean isBikeBookingTimedOut() {
        return ismBookTaken() && (getRemainingBookingTime(getmBookDate()) <= 0);
    }

    public boolean isSlotsBookingTimedOut() {
        return ismSlotsTaken() && (getRemainingBookingTime(getmSlotsDate()) <= 0);
    }

}