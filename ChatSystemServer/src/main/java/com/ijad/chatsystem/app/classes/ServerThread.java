
package com.ijad.chatsystem.app.classes;

import com.ijad.chatsystem.commonclasses.ClientDTO;
import com.ijad.chatsystem.commonclasses.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;


public class ServerThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(ServerThread.class);

    //Map between username and socket
    private LinkedHashMap<String, Socket> onlineUsersMap = new LinkedHashMap<>();
    private ArrayList<String> offlineUsersList = new ArrayList<>();
    //Chat history for every user
    private static Hashtable<String, ArrayList<Message>> messagesBetweenUsersHashTable = new Hashtable<>();

    @Override
    public void run() {
        try {
            int port = 7006;
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("Server is running");
            while (true) {

                //Accepting new clients
                Socket clientSocket = serverSocket.accept();
                InputStream inputStream = clientSocket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                //Link client's socket to their username
                String username = bufferedReader.readLine();
                offlineUsersList.remove(username);
                ClientDTO newUser = new ClientDTO(username, clientSocket.toString());
                onlineUsersMap.put(username, clientSocket);
                logger.info("{} accepted", username);

                //Send chat history to newly connected user
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectOutputStream.writeObject(messagesBetweenUsersHashTable);

                //Send online and offline users to clients
                if (onlineUsersMap.size() > 1) {
                    OnlineUsersManager OnlineUsersManager = new OnlineUsersManager(onlineUsersMap, newUser);
                    OnlineUsersManager.start();
                    OfflineUsersManager offlineUsersManager = new OfflineUsersManager(onlineUsersMap, offlineUsersList);
                    offlineUsersManager.start();
                }

                //Message handler for every client
                MessagesHandler messagesHandler = new MessagesHandler(inputStream, onlineUsersMap, offlineUsersList);
                messagesHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Hashtable<String, ArrayList<Message>> getMessagesBetweenUsersHashTable() {
        return messagesBetweenUsersHashTable;
    }
    
    public LinkedHashMap<String, Socket> getOnlineUsersMap() {
        return onlineUsersMap;
    }

    public ArrayList<String> getOfflineUsersList() {
        return offlineUsersList;
    }
}

//TODO:
// - Receive and save chatSession
// - Retrieve chat session