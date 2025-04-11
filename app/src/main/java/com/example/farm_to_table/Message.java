package com.example.farm_to_table;

public class Message {
    private String text;
    private boolean isSentByUser;
    private String timestamp;

    public Message(String text, boolean isSentByUser, String timestamp) {
        this.text = text;
        this.isSentByUser = isSentByUser;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
