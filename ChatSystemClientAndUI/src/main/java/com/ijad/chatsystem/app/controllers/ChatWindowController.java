package com.ijad.chatsystem.app.controllers;

import com.ijad.chatsystem.app.classes.ClientThread;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.ijad.chatsystem.commonclasses. *;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private Message message;


    /**
     * When the user clicks send button new message is created and send to the server for handling
     */
    public void sendMessage() throws IOException {
        if (messageField.getText().length() <= 200) {
            String chosenUser = onlineUsersListView.getSelectionModel().getSelectedItem();
            String timeStamp = new SimpleDateFormat("HH:mm:ss' on 'dd.MM.yyyy").format(new Date());
            message = new Message(messageField.getText(), ClientThread.getUsername(), chosenUser, timeStamp);

            ClientThread.sendMessage(message);

            StringBuilder messageForDisplay = new StringBuilder();
            messageForDisplay.append(ClientThread.getUsername()).append(": ").append(messageField.getText())
                    .append("\n").append(timeStamp);
            allMessagesArea.appendText(messageForDisplay + "\n" + "\n");
            messageField.clear();

            //TODO: Clear and save textArea when another user is selected
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

    public void exitSystem() {

    }

    public Message getMessage() {
        return message;
    }

    public TextArea getAllMessagesArea() {
        return allMessagesArea;
    }
}