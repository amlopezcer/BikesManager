package com.amlopezc.bikesmanager.entity;

import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class JSONBean implements PropertyChangeListener /*, Parcelable*/ {

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

    public void processHashMD5() {
        HashFunction hashFunction = Hashing.md5();
        HashCode hashCode = hashFunction.hashInt(hashCode());
        md5 = hashCode.toString();
    }

    public String getMd5(){return md5;}

    public abstract int getServerId();

    public abstract void setServerId(int serverId);

}
