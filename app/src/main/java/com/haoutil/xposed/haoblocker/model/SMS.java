package com.haoutil.xposed.haoblocker.model;

import java.io.Serializable;

public class SMS implements Serializable {
    public static final int SMS_UNREADED = 0;
    public static final int SMS_READED = 1;

    private long id;
    private String sender;
    private String content;
    private long created;
    private int read;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
