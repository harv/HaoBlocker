package com.haoutil.xposed.haoblocker.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.haoutil.xposed.haoblocker.BuildConfig;

import de.robv.android.xposed.XSharedPreferences;

public class SettingsHelper {
    private static final String MODULE_NAME = BuildConfig.APPLICATION_ID;

    private SharedPreferences mPreferences = null;
    private XSharedPreferences mXPreferences = null;

    public SettingsHelper() {
        mXPreferences = new XSharedPreferences(MODULE_NAME);
        mXPreferences.makeWorldReadable();
    }

    public SettingsHelper(Context context) {
        mPreferences = context.getSharedPreferences(MODULE_NAME + "_preferences", 1);
    }

    public boolean isEnable() {
        reload();
        return getBoolean("pref_enable", true);
    }

    public void setEnable(boolean value) {
        setBoolean("pref_enable", value);
    }

    public boolean isEnableSMS() {
        reload();
        return getBoolean("pref_sms_enable", true);
    }

    public void setEnableSMS(boolean value) {
        setBoolean("pref_sms_enable", value);
    }

    public boolean isEnableCall() {
        reload();
        return getBoolean("pref_call_enable", true);
    }

    public void setEnableCall(boolean value) {
        setBoolean("pref_call_enable", value);
    }

    public boolean isShowBlockNotification() {
        reload();
        return getBoolean("pref_show_block_notification", true);
    }

    public void setShowBlockNotification(boolean value) {
        setBoolean("pref_show_block_notification", value);
    }

    private void reload() {
        if (mXPreferences != null) {
            mXPreferences.reload();
        }
    }

    private void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getEditor();

        if (editor != null) {
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences pref = getSharedPreferences();

        if (pref != null) {
            return getSharedPreferences().getBoolean(key, defaultValue);
        }

        return defaultValue;
    }

    private SharedPreferences.Editor getEditor() {
        if (mPreferences != null) {
            return mPreferences.edit();
        } else if (mXPreferences != null) {
            return mXPreferences.edit();
        }

        return null;
    }

    private SharedPreferences getSharedPreferences() {
        if (mPreferences != null) {
            return mPreferences;
        } else if (mXPreferences != null) {
            return mXPreferences;
        }

        return null;
    }
}
