package com.ijad.chatsystemclientandui.classes;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientThread extends Thread {

    private final Logger logger = LoggerFactory.getLogger(ClientThread.class);
    private static String username;


    private static List<Object> offlineUsers = new ArrayList<>();
    private static List<Object> onlineUsers = new ArrayList<>();
    private ObjectInputStream objectInputStream = null;
    private PrintWriter printWriter = null;

    public ClientThread(String username) {
        this.username = username;
    }

    @Override
    public void run() {
        try {
            Socket clientSocket = new Socket("localhost", 7005);
            logger.info("Client is running");
            while (true) {
                objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

                //Send username to server
                printWriter.println(username);

                onlineUsers.addAll((Collection<?>) objectInputStream.readObject());
               // System.out.println(onlineUsers);

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getUsername() {
        return username;
    }

    public static List<Object> getOfflineUsers() {
        return offlineUsers;
    }

    public static List<Object> getOnlineUsers() {
        return onlineUsers;
    }
}

//TODO:
// - Create broadcasting for the update of online users
// - Create a message
// - Think about how to test with a created message
// - Think about how to create a chat session history and its components