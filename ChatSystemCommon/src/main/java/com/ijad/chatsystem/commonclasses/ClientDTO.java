package com.ijad.chatsystem.commonclasses;

import java.io.Serializable;

/**
 * Class representing a client.
 * Storing username and socket as String
 */
public class ClientDTO implements Serializable {
    private String username;
    private String socket;


    public ClientDTO(String username, String socket) {
        this.username = username;
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }
}