package com.ijad.chatsystem.app.controllers;

import com.ijad.chatsystem.app.classes.ClientThread;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LogInController {

    private final Logger logger = LoggerFactory.getLogger(LogInController.class);

    @FXML
    private TextField usernameTextField;
    @FXML
    private Button logInButton;

    private String username;
    private static FXMLLoader fxmlLoaderChatWindow;

    /**
     * Checks if username is entered correctly then starts the chat window
     *
     * @throws Exception
     */
    public void logIn() throws Exception {
        String strPattern = "^[a-zA-Z0-9]*$";
        logger.info("Start logIn");
        username = usernameTextField.getText();

        if (username.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter correct username no longer than 10 characters");
            alert.show();
        } else if (username.matches(strPattern) && username.length() <= 10) {
            new ClientThread(username).start();
            startChatWindow();

            Stage stage = (Stage) logInButton.getScene().getWindow();
            stage.close();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter correct username no longer than 10 characters");
            alert.show();

        }
    }

    /**
     * Loads the chat window of the system
     *
     * @throws IOException
     */
    public void startChatWindow() throws IOException {
        fxmlLoaderChatWindow = new FXMLLoader(getClass().getResource("/fxml/ChatWindowUI.fxml"));
        Parent root1 = fxmlLoaderChatWindow.load();
        Stage stage = new Stage();
        stage.setTitle("Welcome " + username);
        stage.setScene(new Scene(root1));
        stage.show();
        stage.setOnCloseRequest(event -> {
            if(!ChatWindowController.exitSystem()){
                event.consume();
            }
        });
    }

    public static FXMLLoader getFxmlLoaderChatWindow() {
        return fxmlLoaderChatWindow;
    }
}