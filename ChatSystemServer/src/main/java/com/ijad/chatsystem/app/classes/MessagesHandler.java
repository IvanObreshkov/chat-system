package com.ijad.chatsystem.app.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.ijad.chatsystem.commonclasses.*;

/**
 * This class is responsible for handling incoming messages and sending them to the appropriate user
 */
public class MessagesHandler extends Thread {

    private InputStream inputStream;
    private LinkedHashMap<String, Socket> onlineUsersMap;

    //  private ArrayList<Message> allMessages = new ArrayList<>();
    private HashMap<String, ArrayList<Message>> messagesSentToUser;


    public MessagesHandler(InputStream inputStream, LinkedHashMap<String, Socket> onlineUsersMap) {
        this.inputStream = inputStream;
        this.onlineUsersMap = onlineUsersMap;
        messagesSentToUser = new LinkedHashMap<>();
    }

    @Override
    public void run() {
        try {
            while (true) {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Message messageForSending = (Message) objectInputStream.readObject();
                Socket clientSocket = onlineUsersMap.get(messageForSending.getReceiver());
               // linkMessagesToUser(messageForSending);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectOutputStream.writeObject(messageForSending);
              //  objectOutputStream.writeObject(messagesSentToUser);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Works only for sent messages to users, not received, maybe send hashmap
    public void linkMessagesToUser(Message message) {
        if (!messagesSentToUser.containsKey(message.getReceiver())) {
            messagesSentToUser.put(message.getReceiver(), new ArrayList<>());
        }
        messagesSentToUser.get(message.getReceiver()).add(message);
    }
}


