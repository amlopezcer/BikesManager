package com.amlopezc.bikesmanager.util;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Non-instantiable utility class which manages utility operations with the OS
 */

public final class DeviceUtilities {

    //Suppress default constructor for non-instantiability
    private DeviceUtilities() {}

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Ensure call the following method when the keyboard is active
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
