package com.ijad.chatsystemclientandui.classes;

import java.net.Socket;

/**
 * This class represents the integral parts of a message:
 * Who is the sender, who is the receiver and the content of the message
 */
public class Message {
    private String content;
    private Socket sender;
    private Socket receiver;

    public Message(String content, Socket sender, Socket receiver) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Socket getSender() {
        return sender;
    }

    public void setSender(Socket sender) {
        this.sender = sender;
    }

    public Socket getReceiver() {
        return receiver;
    }

    public void setReceiver(Socket receiver) {
        this.receiver = receiver;
    }

}
