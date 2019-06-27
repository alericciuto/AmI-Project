package com.example.kmapp.Conversation.MessageManager;

public class ResponseMessage2 {

    String text;
    boolean isMe;

    public ResponseMessage2(String text, boolean isMe) {
        this.text = text;
        this.isMe = isMe;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }
}