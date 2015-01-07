package com.haoutil.xposed.haoblocker.event;

public class RuleUpdateEvent {
    public static final int EVENT_HIDE_DISCARD = 0;
    public static final int EVENT_SHOW_DISCARD = 1;
    public static final int EVENT_CHECK_NONE = 2;
    public static final int EVENT_CHECK_ALL = 3;
    public static final int EVENT_REFRESH_LIST = 4;

    private int event = -1;

    public RuleUpdateEvent(int event) {
        this.event = event;
    }

    public int getEvent() {
        return event;
    }
}
