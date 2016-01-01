package com.amlopezc.bikesmanager.util;

import android.app.Application;
import android.content.Context;

/**
 * General context provider
 */

public class ApplicationContextProvider extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static  Context getContext() { return context; }
}
