package com.amlopezc.bikesmanager.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"balance", "biketaken", "bookaddress", "bookdate", "iduser", "mail", "name", "password", "surname", "telephone", "username" })

public class User {

    @JsonProperty("iduser")
    private String mIdUser;
    @JsonProperty("username")
    private String mUsername;
    @JsonProperty("password")
    private String mPAssword;
    @JsonProperty("name")
    private String mName;
    @JsonProperty("surname")
    private String mSurname;
    @JsonProperty("telephone")
    private String mTelephone;
    @JsonProperty("mail")
    private String mMail;
    @JsonProperty("balance")
    private String mBalance;
    @JsonProperty("bookaddress")
    private String mBookaddress;
    @JsonProperty("bookdate")
    private String mBookdate;
    @JsonProperty("biketaken")
    private String mBiketaken;

    public User() {}

    public User(String mIdUser, String mUsername, String mPAssword, String mName, String mSurname, String mTelephone, String mMail, String mBalance, String mBookaddress, String mBookdate, String mBiketaken) {
        this.mIdUser = mIdUser;
        this.mUsername = mUsername;
        this.mPAssword = mPAssword;
        this.mName = mName;
        this.mSurname = mSurname;
        this.mTelephone = mTelephone;
        this.mMail = mMail;
        this.mBalance = mBalance;
        this.mBookaddress = mBookaddress;
        this.mBookdate = mBookdate;
        this.mBiketaken = mBiketaken;
    }

    public User(String mUsername, String mPAssword, String mName, String mSurname, String mTelephone, String mMail, String mBalance, String mBookaddress, String mBookdate, String mBiketaken) {
        this.mUsername = mUsername;
        this.mPAssword = mPAssword;
        this.mName = mName;
        this.mSurname = mSurname;
        this.mTelephone = mTelephone;
        this.mMail = mMail;
        this.mBalance = mBalance;
        this.mBookaddress = mBookaddress;
        this.mBookdate = mBookdate;
        this.mBiketaken = mBiketaken;
    }

    //<editor-fold desc="GETTERS AND SETTERS">
    public String getmIdUser() {
        return mIdUser;
    }

    public void setmIdUser(String mIdUser) {
        this.mIdUser = mIdUser;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmPAssword() {
        return mPAssword;
    }

    public void setmPAssword(String mPAssword) {
        this.mPAssword = mPAssword;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmSurname() {
        return mSurname;
    }

    public void setmSurname(String mSurname) {
        this.mSurname = mSurname;
    }

    public String getmTelephone() {
        return mTelephone;
    }

    public void setmTelephone(String mTelephone) {
        this.mTelephone = mTelephone;
    }

    public String getmMail() {
        return mMail;
    }

    public void setmMail(String mMail) {
        this.mMail = mMail;
    }

    public String getmBalance() {
        return mBalance;
    }

    public void setmBalance(String mBalance) {
        this.mBalance = mBalance;
    }

    public String getmBookaddress() {
        return mBookaddress;
    }

    public void setmBookaddress(String mBookaddress) {
        this.mBookaddress = mBookaddress;
    }

    public String getmBookdate() {
        return mBookdate;
    }

    public void setmBookdate(String mBookdate) {
        this.mBookdate = mBookdate;
    }

    public String getmBiketaken() {
        return mBiketaken;
    }

    public void setmBiketaken(String mBiketaken) {
        this.mBiketaken = mBiketaken;
    }
    //</editor-fold>


    //<editor-fold desc="EQUALS AND HASHCODE">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!mIdUser.equals(user.mIdUser)) return false;
        if (!mUsername.equals(user.mUsername)) return false;
        if (!mPAssword.equals(user.mPAssword)) return false;
        if (!mName.equals(user.mName)) return false;
        if (!mSurname.equals(user.mSurname)) return false;
        if (!mTelephone.equals(user.mTelephone)) return false;
        if (!mMail.equals(user.mMail)) return false;
        if (!mBalance.equals(user.mBalance)) return false;
        if (!mBookaddress.equals(user.mBookaddress)) return false;
        if (mBookdate != null ? !mBookdate.equals(user.mBookdate) : user.mBookdate != null)
            return false;
        return mBiketaken.equals(user.mBiketaken);

    }

    @Override
    public int hashCode() {
        int result = mIdUser.hashCode();
        result = 31 * result + mUsername.hashCode();
        result = 31 * result + mPAssword.hashCode();
        result = 31 * result + mName.hashCode();
        result = 31 * result + mSurname.hashCode();
        result = 31 * result + mTelephone.hashCode();
        result = 31 * result + mMail.hashCode();
        result = 31 * result + mBalance.hashCode();
        result = 31 * result + mBookaddress.hashCode();
        result = 31 * result + (mBookdate != null ? mBookdate.hashCode() : 0);
        result = 31 * result + mBiketaken.hashCode();
        return result;
    }
    //</editor-fold>
}
