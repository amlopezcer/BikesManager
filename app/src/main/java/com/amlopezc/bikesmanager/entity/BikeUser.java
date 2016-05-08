package com.amlopezc.bikesmanager.entity;


import com.amlopezc.bikesmanager.WelcomeActivity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@JsonPropertyOrder({"balance", "biketaken", "bookaddress", "bookdate", "booktaken", "email",
        "fullname", "id", "md5", "mooringsaddress", "mooringsdate", "mooringstaken", "password",
        "username"})
public class BikeUser extends JSONBean {

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
    @JsonProperty("booktaken")
    private boolean mBookTaken;
    @JsonProperty("mooringstaken")
    private boolean mMooringsTaken;
    @JsonProperty("bookaddress")
    private String mBookAddress;
    @JsonProperty("mooringsaddress")
    private String mMooringsAddress;
    @JsonProperty("bookdate")
    private String mBookDate;
    @JsonProperty("mooringsdate")
    private String mMooringsDate;
    @JsonProperty("balance")
    private float  mBalance;


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

    public boolean ismMooringsTaken() {
        return mMooringsTaken;
    }

    public String getmBookAddress() {
        return mBookAddress;
    }

    public String getmMooringsAddress() {
        return mMooringsAddress;
    }

    public String getmBookDate() {
        return mBookDate;
    }

    public String getmMooringsDate() {
        return mMooringsDate;
    }

    public float getmBalance() {
        return mBalance;
    }

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

    public void setmMooringsTaken(boolean mMooringsTaken) {
        boolean oldValue = this.mMooringsTaken;
        this.mMooringsTaken = mMooringsTaken;
        support.firePropertyChange("mMooringsTaken", oldValue, mMooringsTaken);
    }

    public void setmBookAddress(String mBookAddress) {
        String oldValue = this.mBookAddress;
        this.mBookAddress = mBookAddress;
        support.firePropertyChange("mBookAddress", oldValue, mBookAddress);
    }

    public void setmMooringsAddress(String mMooringsAddress) {
        String oldValue = this.mMooringsAddress;
        this.mMooringsAddress = mMooringsAddress;
        support.firePropertyChange("mMooringsAddress", oldValue, mMooringsAddress);
    }

    public void setmBookDate(String mBookDate) {
        String oldValue = this.mBookDate;
        this.mBookDate = mBookDate;
        support.firePropertyChange("mBookDate", oldValue, mBookDate);
    }

    public void setmMooringsDate(String mMooringsDate) {
        String oldValue = this.mMooringsDate;
        this.mMooringsDate = mMooringsDate;
        support.firePropertyChange("mMooringsDate", oldValue, mMooringsDate);
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
        if (mMooringsTaken != bikeUser.mMooringsTaken) return false;
        if (Float.compare(bikeUser.mBalance, mBalance) != 0) return false;
        if (!mUserName.equals(bikeUser.mUserName)) return false;
        if (!mPassword.equals(bikeUser.mPassword)) return false;
        if (!mFullName.equals(bikeUser.mFullName)) return false;
        if (!mEmail.equals(bikeUser.mEmail)) return false;
        if (!mBookAddress.equals(bikeUser.mBookAddress)) return false;
        if (!mMooringsAddress.equals(bikeUser.mMooringsAddress)) return false;
        if (!mBookDate.equals(bikeUser.mBookDate)) return false;
        return mMooringsDate.equals(bikeUser.mMooringsDate);

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
        result = 31 * result + (mMooringsTaken ? 1 : 0);
        result = 31 * result + mBookAddress.hashCode();
        result = 31 * result + mMooringsAddress.hashCode();
        result = 31 * result + mBookDate.hashCode();
        result = 31 * result + mMooringsDate.hashCode();
        result = 31 * result + (mBalance != +0.0f ? Float.floatToIntBits(mBalance) : 0);
        return result;
    }
    //</editor-fold>

    public void setNewUserData(String mUserName, String mPassword, String mFullName, String mEmail) {
        this.mId = 0; //Not representative, the server will assign an appropriate ID, but this one is needed for serialization
        this.mUserName = mUserName;
        this.mPassword = mPassword;
        this.mFullName = mFullName;
        this.mEmail = mEmail;
        //Some standard data for new users
        this.mBikeTaken = false;
        this.mBookTaken = false;
        this.mMooringsTaken = false;
        this.mBookAddress = "None";
        this.mMooringsAddress = "None";
        this.mBookDate = getCurrentDateFormatted();
        this.mMooringsDate = getCurrentDateFormatted();
        this.mBalance = WelcomeActivity.NEW_USER_PRESENT; //Welcome present: 5.00€
        processHashMD5();
    }

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
