package com.safield.BleeperMaker;

import android.app.Application;
import android.content.Context;

/**
 * Created by Scott on 2/14/2016.
 */
public class LocalApp extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        LocalApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return LocalApp.context;
    }
}
