package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerThread extends Thread {
    private final int port = 7005;
    private ServerSocket serverSocket = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;
    private ArrayList<Object> offlineUsers = new ArrayList<>();
    private ArrayList<Object> onlineUsers = new ArrayList<>();
    private ConcurrentLinkedQueue<Object> messagesForSending;

    @Override
    public void run() {
        try {
            serverSocket  = new ServerSocket(port);
            System.out.println("Server is running");
            while (true) {
                Socket client = serverSocket.accept();
                onlineUsers.add(client);
                System.out.println("Client accepted");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateStatuses() {
    }

    public void sendMessages() {
    }
}

