package com.ijad.chatsystem.app.classes;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * Updates statuses of users by sending lists of online and offline users to all connected clients
 */
public class OnlineUsersManager extends Thread {
    private LinkedHashMap<String, Socket> onlineUsersMap;
    private ArrayList<Socket> usersSockets;
    private ArrayList<String> usernames;
    private ClientDTO newUser;

    public OnlineUsersManager(LinkedHashMap<String, Socket> onlineUsersMap, ClientDTO newUser) {
        this.onlineUsersMap = onlineUsersMap;
        this.newUser = newUser;

        //Separating the onlineUsersMap to ArrayLists of usernames and sockets for handling later
        usernames = new ArrayList<>(onlineUsersMap.keySet());
        usersSockets = new ArrayList<>(onlineUsersMap.values());
    }


    @Override
    public void run() {
        try {

            //Send newUser to all others except him
            for (int i = 0; i < usersSockets.size() - 1; i++) {
                ObjectOutputStream outputStreamToConnectedUser = new ObjectOutputStream((usersSockets.get(i).getOutputStream()));
                outputStreamToConnectedUser.writeObject(newUser);
            }

            //Send newUser all connected users except him
            for (int i = usersSockets.size() - 2; i >= 0; i--) {
                ObjectOutputStream outputStreamToNewUser = new ObjectOutputStream(usersSockets.get(usersSockets.size() - 1).getOutputStream());
                ClientDTO connectedUser = getConnectedUsers(usernames,usersSockets).get(i);
                outputStreamToNewUser.writeObject(connectedUser);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets already connected clients
     * @param usernames
     * @param usersSockets
     * @return ArrayList of connected ClientDTOs
     */
    private ArrayList<ClientDTO> getConnectedUsers(ArrayList<String> usernames, ArrayList<Socket> usersSockets) {
        ArrayList<ClientDTO> connectedUsers = new ArrayList<>();
        for (int i = 0; i < onlineUsersMap.size() - 1; i++) {
            ClientDTO connectedUser = new ClientDTO(usernames.get(i), usersSockets.get(i).toString());
            connectedUsers.add(connectedUser);
        }
        return connectedUsers;
    }

}