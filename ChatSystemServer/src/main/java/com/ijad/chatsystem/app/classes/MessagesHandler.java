package com.ijad.chatsystem.app.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Message messageForSending = (Message) objectInputStream.readObject();
                Socket clientSocket = onlineUsersMap.get(messageForSending.getReceiver());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectOutputStream.writeObject(messageForSending);

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
