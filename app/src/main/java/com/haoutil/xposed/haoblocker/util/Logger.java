package com.haoutil.xposed.haoblocker.util;

import android.util.Log;

import com.haoutil.xposed.haoblocker.BuildConfig;

import de.robv.android.xposed.XposedBridge;

public class Logger {
    private static boolean DEBUG = BuildConfig.DEBUG;

    public static void log(String msg) {
        if (DEBUG) {
            try {
                XposedBridge.log("[HaoBlocker] " + msg);
            } catch (Throwable t) {
                Log.i("[HaoBlocker]", msg);
            }
        }
    }

    public static void log(Throwable t) {
        if (DEBUG) {
            try {
                XposedBridge.log(t);
            } catch (Throwable t1) {
                Log.i("[HaoBlocker]", "", t);
            }
        }
    }
}
