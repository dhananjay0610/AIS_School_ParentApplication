package com.rawtalent.schoolsystemuser.ModelClass;

public class ChatRequestModel {

    String name,chatID;
    long status;

    public ChatRequestModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }
}
