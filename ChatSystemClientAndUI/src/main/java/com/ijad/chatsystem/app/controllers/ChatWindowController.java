package com.ijad.chatsystem.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class ChatWindowController  {
    @FXML
    private TextField messageField;
    @FXML
    private TextArea allMessagesArea;
    @FXML
    private ListView<Object> onlineUsersListView;
    @FXML
    private ListView<Object> offlineUsersListView;

    /**
     * When the user clicks send button
     */
    public void sendMessage() {
        if (messageField.getText().length() <= 200) {
           // displayOnlineUsers(onlineUsersListView);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter message no longer than 200 characters");
            alert.show();
        }
    }

    private void displayOnlineUsers(ListView<Object> onlineUsersListView){
       // onlineUsersListView.getItems().addAll(ClientThread.getOnlineUsers());
      //  onlineUsersListView.getSelectionModel().getSelectedItems();

    }

    private void displayOfflineUsers(){}
}
//FIXME:
// - continuous displaying of onlineUsersList
//TODO: get user, get selected user, get text, create message


