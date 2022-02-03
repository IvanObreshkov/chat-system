package com.ijad.chatsystem.app.classes;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientThread extends Thread {

    private final Logger logger = LoggerFactory.getLogger(ClientThread.class);
    private static String username;
    private static ArrayList<ClientDTO> onlineUsers = new ArrayList<>();
    private static ArrayList<ClientDTO> offlineUsers = new ArrayList<>();

    public ClientThread(String username) {
        this.username = username;
    }

    @Override
    public void run() {
        try {
            Socket clientSocket = new Socket("localhost", 7005);
            logger.info("Client is running");
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

            //Send username to server
            printWriter.println(username);

            while (true) {
                //Receiving connected users
                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                ClientDTO newlyConnectedUser = (ClientDTO) objectInputStream.readObject();
                onlineUsers.add(newlyConnectedUser);

                logger.info("New user " + newlyConnectedUser.getUsername());
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

    public static List<ClientDTO> getOnlineUsers() {
        return onlineUsers;
    }

    public static List<ClientDTO> getOfflineUsers() {
        return offlineUsers;
    }
}

//TODO:
// - Display list of online users
// - Create handling for offline users
// - Display list of offline users
// - Send message
// - Receive message
// - Create a chat session history and its components
// - Retrieve chat session