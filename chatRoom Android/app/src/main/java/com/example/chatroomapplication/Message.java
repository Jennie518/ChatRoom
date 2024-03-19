package com.example.chatroomapplication;

public class Message {
    private String type;
    private String user;
    private String room;
    private String message;
    private String time;

    public Message(String type, String user, String room, String message) {
        this.type = type;
        this.user = user;
        this.room = room;
        this.message = message;
        this.time = time;
    }
}


