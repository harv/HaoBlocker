package com.haoutil.xposed.haoblocker.model;

public class Import {
    // 1:sms,2:call
    private int type;
    // phone number/mobile
    private String number;
    // name in contact(if exist)
    private String name;
    // sms content
    private String content;
    // time of sms/call receded
    private long time;
    // sms/call type(in/out)
    private int inout;
    // selected for importing
    private boolean selected;
    // already query contact name
    private boolean queried;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getInout() {
        return inout;
    }

    public void setInout(int inout) {
        this.inout = inout;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isQueried() {
        return queried;
    }

    public void setQueried(boolean queried) {
        this.queried = queried;
    }
}
