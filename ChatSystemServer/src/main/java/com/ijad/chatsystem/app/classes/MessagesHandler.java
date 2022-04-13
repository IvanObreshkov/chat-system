package com.ijad.chatsystem.app.classes;

import com.ijad.chatsystem.commonclasses.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This class is responsible for handling incoming messages and sending them to the appropriate user
 */
public class MessagesHandler extends Thread {

    private InputStream inputStream;
    private LinkedHashMap<String, Socket> onlineUsersMap;

    public MessagesHandler(InputStream inputStream, LinkedHashMap<String, Socket> onlineUsersMap) {
        this.inputStream = inputStream;
        this.onlineUsersMap = onlineUsersMap;
    }

    @Override
    public void run() {
        try {
            while (true) {
                //Send message to receiver and add it to the server's chat history
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Message messageForSending = (Message) objectInputStream.readObject();
                Socket clientSocket = onlineUsersMap.get(messageForSending.getReceiver());
                linkMessagesToUser(messageForSending);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectOutputStream.writeObject(messageForSending);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create history of messages between two users
     *
     * @param message
     */
    public void linkMessagesToUser(Message message) {
        String sender = message.getSender();
        String receiver = message.getReceiver();
        String key = message.generateKey(sender, receiver);
        if (!ServerThread.getMessagesBetweenUsersHashTable().containsKey(key)) {
            ServerThread.getMessagesBetweenUsersHashTable().put(key, new ArrayList<>());
        }
        ServerThread.getMessagesBetweenUsersHashTable().get(key).add(message);
    }
}