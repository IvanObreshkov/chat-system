package com.ijad.chatsystem.app.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * This class is responsible for handling incoming messages and sending them to the appropriate user
 */
public class MessagesHandler extends Thread {

    private InputStream inputStream;

    public MessagesHandler(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try {

            while (true) {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Message messageForSending = (Message) objectInputStream.readObject();
                System.out.println(messageForSending.getContent());

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
