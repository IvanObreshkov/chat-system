module com.app.clientside.chatsystemclientandui {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;


    exports com.ijad.chatsystem.app.controllers;
    opens com.ijad.chatsystem.app.controllers to javafx.fxml;
}