package app;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(ServerThread.class);
    private final int port = 7005;
    private ServerSocket serverSocket = null;
    private BufferedReader bufferedReader = null;
    private ObjectOutputStream objectOutputStream = null;
    private ArrayList<Object> offlineUsers = new ArrayList<>();
    private HashMap<Object, String> onlineUsers = new HashMap<>();
    private ConcurrentLinkedQueue<Object> messagesForSending;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Server is running");
            while (true) {
                Socket client = serverSocket.accept();
                bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                //Link client's ip to their username
                String username = bufferedReader.readLine();
                onlineUsers.put(client, username);
                logger.info(onlineUsers.get(client) + " " + "accepted");

                //Send online users
                List onlineUsernames = new ArrayList(onlineUsers.values());
                objectOutputStream.writeObject(onlineUsernames);

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

//TODO:
// - Receive and send message from client
// - Receive chatSession