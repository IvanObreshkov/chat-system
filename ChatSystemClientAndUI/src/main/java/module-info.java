module com.app.clientside.chatsystemclientandui {
    requires javafx.controls;
    requires javafx.fxml;


    exports com.ijad.chatsystemclientandui.controllers;
    opens com.ijad.chatsystemclientandui.controllers to javafx.fxml;
}