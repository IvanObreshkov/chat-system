package com.ijad.chatsystemclientandui.classes;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread {
    @FXML
    private TextField usernameTextField;
    private ArrayList<Object> offlineUsers = new ArrayList<>();
    private ArrayList<Object> onlineUsers = new ArrayList<>();
    private BufferedReader bufferedReader = null;
    private PrintWriter printWriter = null;
    Message message;

    public ClientThread(TextField usernameTextField) {
        this.usernameTextField = usernameTextField;
    }

    @Override
    public void run() {
        try {
            //String usernameOfUser = ;
            Socket clientSocket = new Socket("Server",7005);
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Client is running");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
