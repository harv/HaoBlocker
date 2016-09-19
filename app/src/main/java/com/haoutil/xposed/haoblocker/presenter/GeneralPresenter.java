package com.haoutil.xposed.haoblocker.presenter;

public interface GeneralPresenter {
    void initView();

    void enable(boolean enabled);

    void enableSMS(boolean enabled);

    void enableCall(boolean enabled);

    void enableNotification(boolean enabled);
}
