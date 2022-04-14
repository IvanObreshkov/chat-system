package com.ijad.chatsystem.app.classes;

import com.ijad.chatsystem.commonclasses.ClientDTO;
import com.ijad.chatsystem.commonclasses.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private ArrayList<String> offlineUsersList;
    private final Logger logger = LoggerFactory.getLogger(MessagesHandler.class);


    public MessagesHandler(InputStream inputStream, LinkedHashMap<String, Socket> onlineUsersMap, ArrayList<String> offlineUsersList) {
        this.inputStream = inputStream;
        this.onlineUsersMap = onlineUsersMap;
        this.offlineUsersList = offlineUsersList;
    }

    @Override
    public void run() {
        try {
            while (true) {
                //Send message to receiver and add it to the server's chat history
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Message messageForSending = (Message) objectInputStream.readObject();
                if(messageForSending.getMessageType() == Message.MessageType.TEXT) {
                    if(onlineUsersMap.containsKey(messageForSending.getReceiver())) {
                        Socket clientSocket = onlineUsersMap.get(messageForSending.getReceiver());
                        linkMessagesToUser(messageForSending);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                        objectOutputStream.writeObject(messageForSending);
                    }else {
                        linkMessagesToUser(messageForSending);
                    }
                }else{
                    String offlineUserName = messageForSending.getSender();
                    onlineUsersMap.get(messageForSending.getSender()).close();
                    offlineUsersList.add(offlineUserName);
                    onlineUsersMap.remove(offlineUserName);
                    OfflineUsersManager offlineUsersManager = new OfflineUsersManager(onlineUsersMap, offlineUsersList);
                    offlineUsersManager.start();
                    logger.info(offlineUserName + " has disconnected");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("");
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