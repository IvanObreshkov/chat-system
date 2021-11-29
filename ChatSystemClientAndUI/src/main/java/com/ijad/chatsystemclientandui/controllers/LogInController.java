package com.ijad.chatsystemclientandui.controllers;

import com.ijad.chatsystemclientandui.classes.ClientThread;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LogInController {

    private final Logger logger = LoggerFactory.getLogger(LogInController.class);

    @FXML
    private TextField usernameTextField;
    @FXML
    private AnchorPane logInWindow;
    @FXML
    private Button logInButton;

    public void logIn() throws Exception {
        logger.info("Start logIn");
        String username = usernameTextField.getText();
        String strPattern = "^[a-zA-Z0-9]*$";
        if (username.matches(strPattern) && username.length() <= 10) {
            new ClientThread(username).start();
            usernameTextField.setDisable(true);
            logInButton.setDisable(true);
            //TODO: hide logIn window
            startChatWindow();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter correct username no longer than 10 characters");
            alert.show();

        }
    }

    public void startChatWindow() throws IOException {
        AnchorPane chatWindow = FXMLLoader.load(LogInController.class.getResource("/fxml/ChatWindowUI.fxml"));
        logInWindow.getChildren().setAll(chatWindow);
    }

}

//FIXME: ChatWindow show