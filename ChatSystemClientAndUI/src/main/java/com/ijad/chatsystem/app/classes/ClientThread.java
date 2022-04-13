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
import java.net.SocketException;
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
    private static ObjectInputStream objectInputStream;

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
                    System.out.println("closing");
                    return;
                }
                //Receiving connected users
                objectInputStream = new ObjectInputStream(clientSocketInputStream);
                Object receivedObject = objectInputStream.readObject();

                //Checking inputStream for chat history
                if (receivedObject instanceof Hashtable) {
                    chatHistory = (Hashtable<String, ArrayList<Message>>) receivedObject;
                }

                //Checking inputStream for new clients
                else if (receivedObject instanceof ClientDTO) {
                    ClientDTO user = (ClientDTO) receivedObject;

                    if (!onlineUsers.contains(user)) {
                        onlineUsers.add(user);
                        displayOnlineUsers();
                        logger.info("New user " + user.getUsername());
                    } else {
                        offlineUsers.add(user);
                        onlineUsers.remove(user);
                        displayOfflineUsers();
                        logger.info(user.getUsername() + " is now offline");
                    }
                }

                //Checking inputStream for new messages
                else if (receivedObject instanceof Message) {
                    Message receivedMessage = (Message) receivedObject;
                    linkMessagesToUser(receivedMessage);
                    displayNewMessages(receivedMessage);
                }
            }

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
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
        String key = generateKey(sender, receiver);
        if (!chatHistory.containsKey(key)) {
            chatHistory.put(key, new ArrayList<>());
        }
        chatHistory.get(key).add(message);
    }

    /**
     * Generate key regardless of who is sender and receiver.
     * Order receiver and sender alphabetically.
     *
     * @param sender
     * @param receiver
     * @return String key
     */
    public static String generateKey(String sender, String receiver) {
        int compare = sender.compareTo(receiver);
        String key = "";
        if (compare < 0) {
            key = receiver + "-" + sender;
        } else {
            key = sender + "-" + receiver;
        }
        return key;
        // Extract to common
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
        linkMessagesToUser(message);
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

    public static ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }
}

//TODO:
// - Create handling for offline users
// - Display list of offline users
// - Create a chat session history and its components
// - Retrieve chat session