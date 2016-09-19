package com.haoutil.xposed.haoblocker.model.impl;

import android.content.Context;

import com.haoutil.xposed.haoblocker.model.GeneralModel;
import com.haoutil.xposed.haoblocker.util.SettingsHelper;

public class GeneralModelImpl implements GeneralModel {
    private SettingsHelper mSettingsHelper;

    @Override
    public void init(Context context) {
        mSettingsHelper = new SettingsHelper(context);
    }

    @Override
    public boolean isEnable() {
        return mSettingsHelper.isEnable();
    }

    @Override
    public boolean isEnableSMS() {
        return mSettingsHelper.isEnableSMS();
    }

    @Override
    public boolean isEnableCall() {
        return mSettingsHelper.isEnableCall();
    }

    @Override
    public boolean isShowBlockNotification() {
        return mSettingsHelper.isShowBlockNotification();
    }

    @Override
    public void enable(boolean enabled) {
        mSettingsHelper.setEnable(enabled);
    }

    @Override
    public void enableSMS(boolean enabled) {
        mSettingsHelper.setEnableSMS(enabled);
    }

    @Override
    public void enableCall(boolean enabled) {
        mSettingsHelper.setEnableCall(enabled);
    }

    @Override
    public void enableNotification(boolean enabled) {
        mSettingsHelper.setShowBlockNotification(enabled);
    }
}
