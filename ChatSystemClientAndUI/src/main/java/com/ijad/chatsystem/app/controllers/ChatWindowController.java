package com.ijad.chatsystem.app.controllers;

import com.ijad.chatsystem.app.classes.ClientThread;
import com.ijad.chatsystem.commonclasses.Message;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
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

    private static Logger logger = LoggerFactory.getLogger(ClientThread.class);
    private Message message = new Message();
    private final String timeStamp = new SimpleDateFormat("HH:mm:ss' on 'dd.MM.yyyy").format(new Date());

    /**
     * When the user clicks send button new message is created and send to the server for handling
     */
    public void sendMessage() throws IOException {
        if (messageField.getText().length() <= 200) {
            String chosenOnlineUser = onlineUsersListView.getSelectionModel().getSelectedItem();
            String chosenOfflineUser = offlineUsersListView.getSelectionModel().getSelectedItem();
            if (chosenOfflineUser == null) {
                message = new Message(messageField.getText(), ClientThread.getUsername(), chosenOnlineUser, timeStamp);
                ClientThread.sendMessage(message);
            } else if (chosenOnlineUser == null) {
                message = new Message(messageField.getText(), ClientThread.getUsername(), chosenOfflineUser, timeStamp);
                ClientThread.sendMessage(message);
            }

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
        offlineUsersListView.getItems().addAll(ClientThread.getOfflineUsers());
        offlineUsersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /**
     * Displays conversation between currentUser and online one
     */
    public void displayChatHistoryForOnlineUser() {
        String chosenOnlineUser = onlineUsersListView.getSelectionModel().getSelectedItem();
        displayChatHistory(chosenOnlineUser);
    }

    /**
     * Displays conversation between currentUser and offline one
     */
    public void displayChatHistoryForOfflineUser() {
        String chosenOfflineUser = offlineUsersListView.getSelectionModel().getSelectedItem();
        displayChatHistory(chosenOfflineUser);
    }

    /**
     * Displays conversation between users
     */
    private void displayChatHistory(String chosenUser) {
        allMessagesArea.clear();
        try {
            ArrayList<Message> chatHistoryForChosenUser = ClientThread.getChatHistory().get(message.generateKey(chosenUser, ClientThread.getUsername()));
            for (Message message : chatHistoryForChosenUser) {
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
            if (chosenUser.equals(message.getSender()) && !chosenUser.equals(offlineUsersListView.getSelectionModel().getSelectedItem())) {
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

    /**
     * User exits system. A "quit" message is sent to server
     *
     * @return
     */
    public static boolean exitSystem() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Do you want to exit?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                logger.info("Goodbye " + ClientThread.getUsername());
                Message quit = new Message(ClientThread.getUsername());
                ClientThread.sendMessage(quit);
                ClientThread.getClientSocketInputStream().close();
                ClientThread.getClientSocketOutputStream().close();
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

    public void notifyForNewMessages() {
        Alert messageNotification = new Alert(Alert.AlertType.INFORMATION);
        messageNotification.setTitle("New message");
        messageNotification.setContentText(ClientThread.getUsername() + " you have a new messages");
        messageNotification.show();
    }

    public Message getMessage() {
        return message;
    }

    public TextArea getAllMessagesArea() {
        return allMessagesArea;
    }
}
