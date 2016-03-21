package com.haoutil.xposed.haoblocker.model;

public class Call {
    public static final int CALL_UNREADED = 0;
    public static final int CALL_READED = 1;

    private long id;
    private String caller;
    private long created;
    private int read;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }
}
