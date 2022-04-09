package com.ijad.chatsystem.app.classes;

import com.ijad.chatsystem.app.controllers.ChatWindowController;
import com.ijad.chatsystem.app.controllers.LogInController;
import com.ijad.chatsystem.commonclasses.ClientDTO;
import com.ijad.chatsystem.commonclasses.Message;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ClientThread extends Thread {

    private final Logger logger = LoggerFactory.getLogger(ClientThread.class);
    private static String username;
    private static ArrayList<ClientDTO> onlineUsers = new ArrayList<>();
    private static ArrayList<ClientDTO> offlineUsers = new ArrayList<>();
    private ChatWindowController chatWindowController;
    private static InputStream clientSocketInputStream;
    private static OutputStream clientSocketOutputStream;
    private static Socket clientSocket;
    private static Hashtable<String, ArrayList<Message>> chatHistory = new Hashtable<>();

    public ClientThread(String username) {
        ClientThread.username = username;
    }

    @Override
    public void run() {
        try {
            clientSocket = new Socket("localhost", 7006);
            clientSocketOutputStream = clientSocket.getOutputStream();
            clientSocketInputStream = clientSocket.getInputStream();
            logger.info("Client is running");
            PrintWriter printWriter = new PrintWriter(clientSocketOutputStream, true);

            //Send username to server
            printWriter.println(username);

            while (true) {
                //Receiving connected users
                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocketInputStream);
                Object receivedObject = objectInputStream.readObject();

                //Checking inputStream for new clients
                if (receivedObject instanceof ClientDTO) {
                    ClientDTO newlyConnectedUser = (ClientDTO) receivedObject;
                    onlineUsers.add(newlyConnectedUser);

                    //Displaying online users
                    displayOnlineUsers();

                    logger.info("New user " + newlyConnectedUser.getUsername());
                }

                //Gets chat history for every user
                else if (receivedObject instanceof Hashtable) {
                    chatHistory = (Hashtable<String, ArrayList<Message>>) receivedObject;
                }
            }


        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getOnlineUsersUsernames() {
        ArrayList<String> onlineUsersUsernames = new ArrayList<>();
        for (int i = 0; i < onlineUsers.size(); i++) {
            onlineUsersUsernames.add(onlineUsers.get(i).getUsername());
        }
        return onlineUsersUsernames;
    }

    public static void sendMessage(Message message) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocketOutputStream);
        objectOutputStream.writeObject(message);
    }

    private void displayOnlineUsers() {
        Platform.runLater(() -> {
            chatWindowController = LogInController.getFxmlLoaderChatWindow().getController();
            chatWindowController.displayOnlineUsers();

        });
    }

    public static Hashtable<String, ArrayList<Message>> getChatHistory() {
        return chatHistory;
    }

    public static void setChatHistory(Hashtable<String, ArrayList<Message>> chatHistory) {
        ClientThread.chatHistory = chatHistory;
    }

    public static InputStream getClientSocketInputStream() {
        return clientSocketInputStream;
    }

    public static OutputStream getClientSocketOutputStream() {
        return clientSocketOutputStream;
    }

    public static Socket getClientSocket() {
        return clientSocket;
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
// - Create handling for offline users
// - Display list of offline users
// - Create a chat session history and its components
// - Retrieve chat session