package com.ijad.chatsystem.commonclasses;

import java.io.Serializable;

/**
 * This class represents the integral parts of a message:
 * Who is the sender, who is the receiver and the content of the message
 */
public class Message implements Serializable {

    public enum MessageType {TEXT, CLOSE}

    private String content;
    private String sender;
    private String receiver;
    private String timeStamp;
    private MessageType messageType;

    /**
     * Normal text message
     *
     * @param content
     * @param sender
     * @param receiver
     * @param timeStamp
     */
    public Message(String content, String sender, String receiver, String timeStamp) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.timeStamp = timeStamp;
        this.messageType = MessageType.TEXT;
    }

    /**
     * Closing message
     */
    public Message(String sender) {
        this.sender = sender;
        this.messageType = MessageType.CLOSE;
    }

    /**
     * Empty constructor
     */
    public Message(){}

    /**
     * Generate key for chat history regardless of who is sender and receiver.
     * Order receiver and sender lexicographically.
     *
     * @param sender
     * @param receiver
     * @return String key
     */
    public String generateKey(String sender, String receiver) {
        int compare = sender.compareTo(receiver);
        String key = "";
        if (compare < 0) {
            key = receiver + "-" + sender;
        } else {
            key = sender + "-" + receiver;
        }
        return key;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
