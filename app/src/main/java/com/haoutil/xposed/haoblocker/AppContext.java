package com.haoutil.xposed.haoblocker;

import android.app.Application;

public class AppContext extends Application {
    private static AppContext sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static AppContext getsInstance() {
        return sInstance;
    }
}
