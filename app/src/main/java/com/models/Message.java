package com.models;

public class Message {

    private int id;
    private int sender_id;
    private String content;
    private String sender_name;


    public int getSender_id() {
        return sender_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public Message(int sender_id, String content, String sender_name) {
        this.sender_id = sender_id;
        this.content = content;
        this.sender_name = sender_name;
    }
}
