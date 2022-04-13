package com.ijad.chatsystem.app.controllers;

import com.ijad.chatsystem.app.classes.ClientThread;
import com.ijad.chatsystem.commonclasses.Message;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

public class ChatWindowController {
    @FXML
    private Button sendButton;
    @FXML
    private TextField messageField;
    @FXML
    private TextArea allMessagesArea;
    @FXML
    private ListView<String> onlineUsersListView;
    @FXML
    private ListView<String> offlineUsersListView;

    private Message message = new Message();
    private final String timeStamp = new SimpleDateFormat("HH:mm:ss' on 'dd.MM.yyyy").format(new Date());


    /**
     * When the user clicks send button new message is created and send to the server for handling
     */
    public void sendMessage() throws IOException {
        if (messageField.getText().length() <= 200) {
            String chosenUser = onlineUsersListView.getSelectionModel().getSelectedItem();
            message = new Message(messageField.getText(), ClientThread.getUsername(), chosenUser, timeStamp);
            ClientThread.sendMessage(message);

            //Visualize sent message
            StringBuilder messageForDisplay = new StringBuilder();
            messageForDisplay.append(ClientThread.getUsername()).append(": ").append(messageField.getText())
                    .append("\n").append(timeStamp);
            allMessagesArea.appendText(messageForDisplay + "\n" + "\n");
            messageField.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter message no longer than 200 characters");
            alert.show();
        }
    }

    /**
     * Updates the GUI list of online users
     */
    public void displayOnlineUsers() {
        onlineUsersListView.getItems().clear();
        onlineUsersListView.getItems().addAll(ClientThread.getOnlineUsersUsernames());
        onlineUsersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /**
     * Updates the GUI list of offline users
     */
    public void displayOfflineUsers() {
        offlineUsersListView.getItems().clear();
        offlineUsersListView.getItems().addAll(ClientThread.getOnlineUsersUsernames());
        offlineUsersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /**
     * Displays conversation between users
     */
    public void displayChatHistory() {
        String chosenUser = onlineUsersListView.getSelectionModel().getSelectedItem();
        allMessagesArea.clear();
        try {
            ArrayList<Message> chatHistoryForChosenUser = ClientThread.getChatHistory().get(message.generateKey(chosenUser, ClientThread.getUsername()));

            for (int i = 0; i < chatHistoryForChosenUser.size(); i++) {
                Message message = chatHistoryForChosenUser.get(i);
                StringBuilder messageForDisplay = new StringBuilder();
                messageForDisplay.append(message.getSender()).append(": ").append(message.getContent())
                        .append("\n").append(message.getTimeStamp());
                allMessagesArea.appendText(messageForDisplay + "\n" + "\n");
            }
        } catch (NullPointerException e) {
            System.out.println("New conversation started with " + chosenUser);
        }
    }

    /**
     * Display new messages
     */
    public void displayNewMessages(Message message) {
        String chosenUser = onlineUsersListView.getSelectionModel().getSelectedItem();
        if (chosenUser != null) {
            if (chosenUser.equals(message.getSender())) {
                StringBuilder messageForDisplay = new StringBuilder();
                messageForDisplay.append(message.getSender()).append(": ").append(message.getContent())
                        .append("\n").append(message.getTimeStamp());
                allMessagesArea.appendText(messageForDisplay + "\n" + "\n");
            }
        }
        Alert messageNotification = new Alert(Alert.AlertType.INFORMATION);
        messageNotification.setTitle("New message");
        messageNotification.setContentText(ClientThread.getUsername() + " you have a new message from " + message.getSender());
        messageNotification.show();
    }

    public static boolean exitSystem() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Do you want to exit?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                System.out.println("Closing " + ClientThread.getUsername());
                Message quit = new Message();
                ClientThread.sendMessage(quit);
                ClientThread.getObjectInputStream().close();
                // ClientThread.getClientSocketOutputStream().close();
                ClientThread.getClientSocket().close();


                return true;

            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Message getMessage() {
        return message;
    }

    public TextArea getAllMessagesArea() {
        return allMessagesArea;
    }
}
