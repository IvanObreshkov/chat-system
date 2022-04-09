package com.ijad.chatsystem.app.controllers;

import com.ijad.chatsystem.app.classes.ClientThread;
import com.ijad.chatsystem.commonclasses.Message;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
     * Displays conversation between users 
     */
    public void displayChatHistory() {
        allMessagesArea.clear();
        String chosenUser = onlineUsersListView.getSelectionModel().getSelectedItem();
        ArrayList<Message> chatHistoryForChosenUser = ClientThread.getChatHistory().get(generateKey(chosenUser));

        for (int i = 0; i < chatHistoryForChosenUser.size(); i++) {
            Message message = chatHistoryForChosenUser.get(i);
            StringBuilder messageForDisplay = new StringBuilder();
            messageForDisplay.append(message.getSender()).append(": ").append(message.getContent())
                    .append("\n").append(message.getTimeStamp());
            allMessagesArea.appendText(messageForDisplay + "\n" + "\n");
        }
    }
    
    public String generateKey(String chosenUser){
        ArrayList<String> listForOrdering = new ArrayList();
        listForOrdering.add(chosenUser);
        listForOrdering.add(ClientThread.getUsername());
        Collections.sort(listForOrdering);
        String key = listForOrdering.get(0) + "-" + listForOrdering.get(1);
        return key;
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
