package com.ijad.chatsystem.app.classes;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(ServerThread.class);

    //Map between username and socket
    private LinkedHashMap<String, Socket> onlineUsersMap = new LinkedHashMap<>();
    private ArrayList<Object> offlineUsers = new ArrayList<>();
    private ConcurrentLinkedQueue<Object> messagesForSending;

    @Override
    public void run() {
        try {
            int port = 7005;
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("Server is running");
            while (true) {
                //Accepting new clients
                Socket clientSocket = serverSocket.accept();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                //Link client's socket to their username
                String username = bufferedReader.readLine();
                ClientDTO newUser = new ClientDTO(username,clientSocket.toString());
                onlineUsersMap.put(username, clientSocket);
                logger.info("{} accepted", username);

                //Send online users to clients
                if (onlineUsersMap.size() > 1) {
                    OnlineUsersManager onlineUsersManager = new OnlineUsersManager(onlineUsersMap, newUser);
                    onlineUsersManager.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // public void sendMessages() {
        //}
    }
}

//TODO:
// - Create handling for offline users
// - Receive and send message from client to another one
// - Receive and save chatSession
// - Retrieve chat session