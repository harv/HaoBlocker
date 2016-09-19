package com.haoutil.xposed.haoblocker.model;

public interface GeneralModel {
    boolean isEnable();

    boolean isEnableSMS();

    boolean isEnableCall();

    boolean isShowBlockNotification();

    void enable(boolean enabled);

    void enableSMS(boolean enabled);

    void enableCall(boolean enabled);

    void enableNotification(boolean enabled);
}
