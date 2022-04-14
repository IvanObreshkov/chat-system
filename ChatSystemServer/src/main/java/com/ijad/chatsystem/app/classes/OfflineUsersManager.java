package com.ijad.chatsystem.app.classes;

import com.ijad.chatsystem.commonclasses.ClientDTO;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class OfflineUsersManager extends Thread {
    private LinkedHashMap<String, Socket> onlineUsersMap;
    private ArrayList<String> offlineUsersList;
    private ArrayList<Socket> usersSockets;
    private ArrayList<String> usernames;

    public OfflineUsersManager(LinkedHashMap<String, Socket> onlineUsersMap, ArrayList<String> offlineUsersListr) {
        this.onlineUsersMap = onlineUsersMap;
        this.offlineUsersList = offlineUsersListr;

        //Separating the offlineUsersMap to ArrayLists of usernames and sockets for handling later
        usernames = new ArrayList<>(onlineUsersMap.keySet());
        usersSockets = new ArrayList<>(onlineUsersMap.values());
    }


    @Override
    public void run() {
        try {
            //Send offline users to all online others
            for (int i = 0; i < usersSockets.size(); i++) {
                for (int j = 0; j < offlineUsersList.size(); j++) {
                    synchronized ((usersSockets.get(i).getOutputStream())) {
                        ObjectOutputStream outputStreamToConnectedUser = new ObjectOutputStream((usersSockets.get(i).getOutputStream()));
                        outputStreamToConnectedUser.writeObject(offlineUsersList.get(j));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
