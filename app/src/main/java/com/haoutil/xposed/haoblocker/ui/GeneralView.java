package com.haoutil.xposed.haoblocker.ui;

import android.content.Context;

public interface GeneralView {
    Context getApplicationContext();

    void setOnCheckedChangeListener();

    void setAbout();

    void check(boolean checked);

    void checkSMS(boolean checked);

    void checkCall(boolean checked);

    void checkNotification(boolean checked);

    void enable(boolean enabled);
}
