package com.haoutil.xposed.haoblocker.ui;

public interface GeneralView extends BaseView {
    void setOnCheckedChangeListener();

    void setAbout();

    void check(boolean checked);

    void checkSMS(boolean checked);

    void checkCall(boolean checked);

    void checkNotification(boolean checked);

    void enable(boolean enabled);
}
