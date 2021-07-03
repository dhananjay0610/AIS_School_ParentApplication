package com.rawtalent.schoolsystemuser.ModelClass;

public class ModelContacts {

    String name,chatID,lastMessage;
    long status;
    long unseenMessages;
    long lastmsgdate;


    public ModelContacts() {
    }

    public long getLastmsgdate() {
        return lastmsgdate;
    }

    public void setLastmsgdate(long lastmsgdate) {
        this.lastmsgdate = lastmsgdate;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public long getUnseenMessages() {
        return unseenMessages;
    }

    public void setUnseenMessages(long unseenMessages) {
        this.unseenMessages = unseenMessages;
    }
}
