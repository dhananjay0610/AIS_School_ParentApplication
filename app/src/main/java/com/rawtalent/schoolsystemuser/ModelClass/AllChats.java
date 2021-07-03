package com.rawtalent.schoolsystemuser.ModelClass;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class AllChats {


    String name,uid;
    List<DocumentSnapshot> documents;

    int size;
    String lastmsg;

    public AllChats() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<DocumentSnapshot> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentSnapshot> documents) {
        this.documents = documents;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getLastmsg() {
        return lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }
}
