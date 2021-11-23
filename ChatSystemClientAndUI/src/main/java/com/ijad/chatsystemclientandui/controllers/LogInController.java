package com.ijad.chatsystemclientandui.controllers;

import com.ijad.chatsystemclientandui.classes.ClientThread;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class LogInController {
    @FXML
    private TextField usernameTextField;

    public void logIn(){
        String username= usernameTextField.getText();
        String strPattern = "^[a-zA-Z0-9]*$";
        if(username.matches(strPattern) && username.length()<=10){
            new ClientThread(usernameTextField).start();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter correct username no longer than 10 characters");
            alert.show();

        }
    }
}