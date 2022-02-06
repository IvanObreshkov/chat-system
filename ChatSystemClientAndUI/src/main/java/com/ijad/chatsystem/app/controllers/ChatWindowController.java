package com.ijad.chatsystem.app.controllers;

import com.ijad.chatsystem.app.classes.ClientThread;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;


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

    /**
     * When the user clicks send button
     */
    public void sendMessage() {
        if (messageField.getText().length() <= 200) {
            System.out.println("test");
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
        onlineUsersListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String username = onlineUsersListView.getSelectionModel().getSelectedItem();
                System.out.println("Selected " + username);
            }
        });
    }

    public void exitSystem() {

    }


}

//TODO: get user, get selected user, get text, create message