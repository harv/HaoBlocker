package com.haoutil.xposed.haoblocker.event;

public class CallUpdateEvent {
    private int what = -1;

    public CallUpdateEvent(int what) {
        this.what = what;
    }

    public int getWhat() {
        return what;
    }
}
