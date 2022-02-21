package com.ijad.chatsystem.commonclasses;

import java.io.Serializable;

/**
 * This class represents the integral parts of a message:
 * Who is the sender, who is the receiver and the content of the message
 */
public class Message implements Serializable {

    private String content;
    private String sender;
    private String receiver;
    private String timeStamp;

    public Message(String content, String sender, String receiver, String timeStamp) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.timeStamp = timeStamp;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
