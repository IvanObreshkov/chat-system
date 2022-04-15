package com.ijad.chatsystem.app.classes;

import com.ijad.chatsystem.app.controllers.ChatWindowController;
import com.ijad.chatsystem.app.controllers.LogInController;
import com.ijad.chatsystem.commonclasses.ClientDTO;
import com.ijad.chatsystem.commonclasses.Message;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;

public class ClientThread extends Thread {

    private final Logger logger = LoggerFactory.getLogger(ClientThread.class);
    private static String username;
    private static ArrayList<ClientDTO> onlineUsers = new ArrayList<>();
    private static ArrayList<String> offlineUsers = new ArrayList<>();
    private ChatWindowController chatWindowController;
    private static InputStream clientSocketInputStream;
    private static OutputStream clientSocketOutputStream;
    private static Socket clientSocket;
    private static Hashtable<String, ArrayList<Message>> localChatHistory = new Hashtable<>();

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
                if (clientSocket.isClosed()) {
                    logger.info("closing");
                    return;
                }
                //Receiving connected users
                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocketInputStream);
                Object receivedObject = objectInputStream.readObject();

                //Checking inputStream for chat history
                if (receivedObject instanceof Hashtable) {
                    localChatHistory = (Hashtable<String, ArrayList<Message>>) receivedObject;
                }

                //Checking inputStream for new clients
                else if (receivedObject instanceof ClientDTO) {
                    ClientDTO user = (ClientDTO) receivedObject;

                    if (offlineUsers.contains(user.getUsername())) {
                        offlineUsers.remove(user.getUsername());
                        displayOfflineUsers();
                    }
                    onlineUsers.add(user);
                    displayOnlineUsers();
                    logger.info("New user " + user.getUsername());
                }

                //Checking inputStream for new messages
                else if (receivedObject instanceof Message) {
                    Message receivedMessage = (Message) receivedObject;
                    linkMessagesToUser(receivedMessage);
                    displayNewMessages(receivedMessage);
                }

                //Checks if a user has gone offline
                else if (receivedObject instanceof String) {
                    String offlineUsername = (String) receivedObject;
                    for (int i = 0; i < onlineUsers.size(); i++) {

                        if (onlineUsers.get(i).getUsername().equals(offlineUsername)) {
                            offlineUsers.add(onlineUsers.get(i).getUsername());
                            onlineUsers.remove(onlineUsers.get(i));
                            displayOnlineUsers();
                            displayOfflineUsers();
                            logger.info(offlineUsername + " is now offline");
                        }
                    }
                    if (!offlineUsers.contains(offlineUsername)) {
                        offlineUsers.add(offlineUsername);
                        displayOnlineUsers();
                        displayOfflineUsers();
                    }

                }

            }

        } catch (ConnectException e) {
            logger.info("The server is offline");
        } catch (SocketException e) {
            logger.info("Closing " + ClientThread.getUsername());
        } catch (IOException | ClassNotFoundException e) {
            logger.info("Server crashed");
        }
    }

    /**
     * Create history of messages between two users
     *
     * @param message
     */
    public static void linkMessagesToUser(Message message) {
        String sender = message.getSender();
        String receiver = message.getReceiver();
        String key = message.generateKey(sender, receiver);
        if (!localChatHistory.containsKey(key)) {
            localChatHistory.put(key, new ArrayList<>());
        }
        localChatHistory.get(key).add(message);
    }

    public static ArrayList<String> getOnlineUsersUsernames() {
        ArrayList<String> onlineUsersUsernames = new ArrayList<>();
        for (ClientDTO onlineUser : onlineUsers) {
            onlineUsersUsernames.add(onlineUser.getUsername());
        }
        return onlineUsersUsernames;
    }

    public static void sendMessage(Message message) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocketOutputStream);
        objectOutputStream.writeObject(message);
        if (message.getMessageType() == Message.MessageType.TEXT) {
            linkMessagesToUser(message);
        }
    }

    private void displayNewMessages(Message message) {
        Platform.runLater(() -> {
            chatWindowController = LogInController.getFxmlLoaderChatWindow().getController();
            chatWindowController.displayNewMessages(message);
        });
    }

    private void displayOnlineUsers() {
        Platform.runLater(() -> {
            chatWindowController = LogInController.getFxmlLoaderChatWindow().getController();
            chatWindowController.displayOnlineUsers();
        });
    }

    private void displayOfflineUsers() {
        Platform.runLater(() -> {
            chatWindowController = LogInController.getFxmlLoaderChatWindow().getController();
            chatWindowController.displayOfflineUsers();
        });
    }

    public static Hashtable<String, ArrayList<Message>> getLocalChatHistory() {
        return localChatHistory;
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

    public static ArrayList<String> getOfflineUsers() {
        return offlineUsers;
    }
}
