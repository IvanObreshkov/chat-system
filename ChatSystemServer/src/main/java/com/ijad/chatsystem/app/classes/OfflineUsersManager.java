package com.ijad.chatsystem.app.classes;

import com.ijad.chatsystem.commonclasses.ClientDTO;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class OfflineUsersManager extends Thread {
    private LinkedHashMap<String, Socket> offlineUsersMap;
    private ArrayList<Socket> usersSockets;
    private ArrayList<String> usernames;
    private ClientDTO disconnectedUser;

    public OfflineUsersManager(LinkedHashMap<String, Socket> offlineUsersMap, ClientDTO disconnectedUser) {
        this.offlineUsersMap = offlineUsersMap;
        this.disconnectedUser = disconnectedUser;

        //Separating the offlineUsersMap to ArrayLists of usernames and sockets for handling later
        usernames = new ArrayList<>(offlineUsersMap.keySet());
        usersSockets = new ArrayList<>(offlineUsersMap.values());
    }


    @Override
    public void run() {
        try {
            //Send disconnectedUser to all others
            for (int i = 0; i < usersSockets.size() - 1; i++) {
                ObjectOutputStream outputStreamToConnectedUser = new ObjectOutputStream((usersSockets.get(i).getOutputStream()));
                outputStreamToConnectedUser.writeObject(disconnectedUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  // /**
  //  * Gets already connected clients
  //  * @param usernames
  //  * @param usersSockets
  //  * @return ArrayList of connected ClientDTOs
  //  */
  // private ArrayList<ClientDTO> getOfflineUsers(ArrayList<String> usernames, ArrayList<Socket> usersSockets) {
  //     ArrayList<ClientDTO> offlineUsers = new ArrayList<>();
  //     for (int i = 0; i < offlineUsersMap.size() - 1; i++) {
  //         ClientDTO connectedUser = new ClientDTO(usernames.get(i), usersSockets.get(i).toString());
  //         offlineUsers.add(connectedUser);
  //     }
  //     return offlineUsers;
  // }
}
