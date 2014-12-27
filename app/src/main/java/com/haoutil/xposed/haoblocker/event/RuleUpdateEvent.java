package com.haoutil.xposed.haoblocker.event;

public class RuleUpdateEvent {
    private int what = -1;

    public RuleUpdateEvent(int what) {
        this.what = what;
    }

    public int getWhat() {
        return what;
    }
}
