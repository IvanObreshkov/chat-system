package com.ijad.chatsystem.app.classes;

import com.ijad.chatsystem.app.controllers.ChatWindowController;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ijad.chatsystem.commonclasses.*;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.ijad.chatsystem.app.controllers.LogInController.fxmlLoaderChatWindow;

public class ClientThread extends Thread {

    private final Logger logger = LoggerFactory.getLogger(ClientThread.class);
    private static String username;
    private static ArrayList<ClientDTO> onlineUsers = new ArrayList<>();
    //Make observable
    private static ArrayList<ClientDTO> offlineUsers = new ArrayList<>();
    //maybe make private
    public static boolean running = false;
    private ChatWindowController chatWindowController;
    private static InputStream clientSocketInputStream;
    private static OutputStream clientSocketOutputStream;
    private static Socket clientSocket;
    private static HashMap<String, ArrayList<Message>> chatHistory;
    private Message receivedMessage;

    public ClientThread(String username) {
        ClientThread.username = username;
    }

    @Override
    public void run() {
        try {
            running = true;
            clientSocket = new Socket("localhost", 7005);
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

                    //Checking inputStream for new messages
                } else if (receivedObject instanceof Message) {
                    Message receivedMessage = (Message) receivedObject;

                    StringBuilder messageForDisplay = new StringBuilder();
                    messageForDisplay.append(receivedMessage.getSender()).append(": ").append(receivedMessage.getContent())
                            .append("\n").append(receivedMessage.getTimeStamp());
                    chatWindowController.getAllMessagesArea().appendText(messageForDisplay + "\n" + "\n");

                    //Adds received messages to hashmap containing chat history for every user
                }// else if (receivedObject instanceof HashMap) {
                 //   chatHistory = (HashMap<String, ArrayList<Message>>) receivedObject;
                 //   chatHistory.get(receivedMessage.getSender()).add(receivedMessage);
                 //   }


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
            chatWindowController = fxmlLoaderChatWindow.getController();
            chatWindowController.displayOnlineUsers();
        });
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
// - Send message
// - Receive message
// - Create a chat session history and its components
// - Retrieve chat session