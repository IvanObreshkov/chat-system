package com.ijad.chatsystem.app.classes;

public class Main {

    public static void main(String[] args) {
        ServerThread serverThread = new ServerThread();
        serverThread.start();
    }
}
