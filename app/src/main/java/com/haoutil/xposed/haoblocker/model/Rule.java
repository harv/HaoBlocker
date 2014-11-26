package com.haoutil.xposed.haoblocker.model;

import java.io.Serializable;

public class Rule implements Serializable {
    public static final int TYPE_STRING = 0;
    public static final int TYPE_WILDCARD = 1;
    public static final int TYPE_KEYWORD = 2;

    private long id;
    private String content;
    private int type;
    private int sms;
    private int call;
    private long created;
    private boolean checked;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSms() {
        return sms;
    }

    public void setSms(int sms) {
        this.sms = sms;
    }

    public int getCall() {
        return call;
    }

    public void setCall(int call) {
        this.call = call;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
