package com.ijad.chatsystem.app.classes;

import com.ijad.chatsystem.app.controllers.ChatWindowController;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.ijad.chatsystem.app.controllers.LogInController.fxmlLoaderChatWindow;

public class ClientThread extends Thread {

    private final Logger logger = LoggerFactory.getLogger(ClientThread.class);
    private static String username;
    private static ArrayList<ClientDTO> onlineUsers = new ArrayList<>();
    private static ArrayList<ClientDTO> offlineUsers = new ArrayList<>();
    public static boolean running = false;

    public ClientThread(String username) {
        ClientThread.username = username;
    }

    @Override
    public void run() {
        try {
            running = true;
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

                //Displaying online users
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ChatWindowController chatWindowController = fxmlLoaderChatWindow.getController();
                        chatWindowController.displayOnlineUsers();
                    }
                });

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

    public static ArrayList<String> getOnlineUsersUsernames() {
        ArrayList<String> onlineUsersUsernames = new ArrayList<>();
        for (int i = 0; i < onlineUsers.size(); i++) {
            onlineUsersUsernames.add(onlineUsers.get(i).getUsername());
        }
        return onlineUsersUsernames;
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