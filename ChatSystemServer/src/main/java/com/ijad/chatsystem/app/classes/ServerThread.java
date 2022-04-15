
package com.ijad.chatsystem.app.classes;

import com.ijad.chatsystem.commonclasses.ClientDTO;
import com.ijad.chatsystem.commonclasses.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;


public class ServerThread extends Thread {
    public static final String CHAT_HISTORY_PATH = "/home/vankata/Documents/chatHistory.ser";
    public static final String OFFLINE_USERS_PATH = "/home/vankata/Documents/offlineUsers.ser";


    private final Logger logger = LoggerFactory.getLogger(ServerThread.class);

    //Map between username and socket
    private LinkedHashMap<String, Socket> onlineUsersMap = new LinkedHashMap<>();
    private static ArrayList<String> offlineUsersList = new ArrayList<>();
    //Chat history for every user
    private static Hashtable<String, ArrayList<Message>> messagesBetweenUsersHashTable = new Hashtable<>();

    @Override
    public void run() {
        try {
            int port = 7006;
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("Server is running");

            if (!Files.exists(Path.of(CHAT_HISTORY_PATH))) {
                serialize(messagesBetweenUsersHashTable, CHAT_HISTORY_PATH);
            }

            if (!Files.exists(Path.of(OFFLINE_USERS_PATH))) {
                serialize(offlineUsersList, OFFLINE_USERS_PATH);
            }

            while (true) {

                //Accepting new clients
                Socket clientSocket = serverSocket.accept();
                InputStream inputStream = clientSocket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                messagesBetweenUsersHashTable = (Hashtable<String, ArrayList<Message>>) deserialize(CHAT_HISTORY_PATH);
                offlineUsersList = (ArrayList<String>) deserialize(OFFLINE_USERS_PATH);
                //maybe make static

                //Link client's socket to their username
                String username = bufferedReader.readLine();
                offlineUsersList.remove(username);
                ClientDTO newUser = new ClientDTO(username, clientSocket.toString());
                onlineUsersMap.put(username, clientSocket);
                logger.info("{} accepted", username);

                //Send chat history to newly connected user
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectOutputStream.writeObject(messagesBetweenUsersHashTable);

                OfflineUsersManager offlineUsersManager = new OfflineUsersManager(onlineUsersMap);
                offlineUsersManager.start();
                offlineUsersManager.join();

                //Send online and offline users to clients
                OnlineUsersManager onlineUsersManager = new OnlineUsersManager(onlineUsersMap, newUser);
                onlineUsersManager.start();


                //Message handler for every client
                MessagesHandler messagesHandler = new MessagesHandler(inputStream, onlineUsersMap);
                messagesHandler.start();

            }
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Serialize the chat history object and save it to file
     *
     * @param obj
     * @param fileName
     * @throws IOException
     */
    public static void serialize(Object obj, String fileName) throws IOException {
        ObjectOutputStream objectOut = new ObjectOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(fileName)));
        objectOut.writeObject(obj);
        objectOut.close();
    }

    /**
     * Deserialize chat history from file
     *
     * @param fileName
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream objectIn = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(fileName)));
        Object myObj = objectIn.readObject();
        objectIn.close();
        return myObj;
    }

    public static Hashtable<String, ArrayList<Message>> getMessagesBetweenUsersHashTable() {
        return messagesBetweenUsersHashTable;
    }

    public LinkedHashMap<String, Socket> getOnlineUsersMap() {
        return onlineUsersMap;
    }

    public static ArrayList<String> getOfflineUsersList() {
        return offlineUsersList;
    }
}

//TODO:
// - Receive and save chatSession
// - Retrieve chat session