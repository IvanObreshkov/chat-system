
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
    private static LinkedHashMap<String, Socket> onlineUsersMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, Socket> offlineUsersMap = new LinkedHashMap<>();
    //Chat history for every user
    private static Hashtable<String, ArrayList<Message>> messagesBetweenUsersHashTable = new Hashtable<>();

    @Override
    public void run() {
        try {
            int port = 7006;
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("Server is running");
            while (true) {
                /*
                //Handling disconnected users
                for (String s : onlineUsersMap.keySet()) {
                    if (onlineUsersMap.get(s).isClosed()) {
                        offlineUsersMap.put(s, onlineUsersMap.get(s));
                        ClientDTO disconnectedUser = new ClientDTO(s, onlineUsersMap.get(s).toString());
                        onlineUsersMap.remove(s);
                        OfflineUsersManager offlineUsersManager = new OfflineUsersManager(ServerThread.getOfflineUsersMap(), disconnectedUser);
                        offlineUsersManager.start();
                        System.out.println(s + " has disconnected");
                    }
                }
                */

                //Accepting new clients
                Socket clientSocket = serverSocket.accept();
                InputStream inputStream = clientSocket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                //Link client's socket to their username
                String username = bufferedReader.readLine();
                ClientDTO newUser = new ClientDTO(username, clientSocket.toString());
                onlineUsersMap.put(username, clientSocket);
                logger.info("{} accepted", username);

                //Send chat history to newly connected user
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectOutputStream.writeObject(ServerThread.getMessagesBetweenUsersHashTable());

                //Send online users to clients
                if (onlineUsersMap.size() > 1) {
                    OnlineUsersManager onlineUsersManager = new OnlineUsersManager(onlineUsersMap, newUser);
                    onlineUsersManager.start();

                }

                //Message handler for every client
                MessagesHandler messagesHandler = new MessagesHandler(inputStream, onlineUsersMap);
                messagesHandler.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Hashtable<String, ArrayList<Message>> getMessagesBetweenUsersHashTable() {
        return messagesBetweenUsersHashTable;
    }

    public static void setMessagesBetweenUsersHashTable(Hashtable<String, ArrayList<Message>> messagesBetweenUsersHashTable) {
        ServerThread.messagesBetweenUsersHashTable = messagesBetweenUsersHashTable;
    }

    public static LinkedHashMap<String, Socket> getOnlineUsersMap() {
        return onlineUsersMap;
    }

    public static void setOnlineUsersMap(LinkedHashMap<String, Socket> onlineUsersMap) {
        ServerThread.onlineUsersMap = onlineUsersMap;
    }

    public static LinkedHashMap<String, Socket> getOfflineUsersMap() {
        return offlineUsersMap;
    }

    public static void setOfflineUsersMap(LinkedHashMap<String, Socket> offlineUsersMap) {
        ServerThread.offlineUsersMap = offlineUsersMap;
    }
}

//TODO:
// - Create handling for offline users
// - Receive and save chatSession
// - Retrieve chat session