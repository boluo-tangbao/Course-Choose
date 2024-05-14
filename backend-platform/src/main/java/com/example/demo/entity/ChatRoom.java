package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private String roomId;
    private List<String> messages;

    public ChatRoom(String roomId,ArrayList<String> messages) {
        this.roomId = roomId;
        this.messages = messages;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }
}
