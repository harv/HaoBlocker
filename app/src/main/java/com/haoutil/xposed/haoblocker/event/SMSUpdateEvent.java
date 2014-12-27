package com.haoutil.xposed.haoblocker.event;

public class SMSUpdateEvent {
    private int what = -1;

    public SMSUpdateEvent(int what) {
        this.what = what;
    }

    public int getWhat() {
        return what;
    }
}
