package com.ijad.chatsystemclientandui.controllers;

import com.ijad.chatsystemclientandui.classes.ClientThread;
import com.ijad.chatsystemclientandui.classes.Message;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.LoggerFactory;


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
            System.out.println("Chat works fine");
            displayOnlineUsers(onlineUsersListView);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter message no longer than 200 characters");
            alert.show();
        }
    }

    private void displayOnlineUsers(ListView<Object> onlineUsersListView){
        onlineUsersListView.getItems().addAll(ClientThread.getOnlineUsers());
      //  onlineUsersListView.getSelectionModel().getSelectedItems();
        System.out.println(onlineUsersListView.getSelectionModel().getSelectedItems());
    }

    private void displayOfflineUsers(){}
}
//FIXME:
// - continuous displaying of onlineUsersList


