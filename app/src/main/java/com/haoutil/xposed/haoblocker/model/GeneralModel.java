package com.haoutil.xposed.haoblocker.model;

import android.content.Context;

public interface GeneralModel {
    void init(Context context);

    boolean isEnable();

    boolean isEnableSMS();

    boolean isEnableCall();

    boolean isShowBlockNotification();

    void enable(boolean enabled);

    void enableSMS(boolean enabled);

    void enableCall(boolean enabled);

    void enableNotification(boolean enabled);
}
