module com.app.clientside.chatsystemclientandui {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires ChatSystemCommon;


    exports com.ijad.chatsystem.app.controllers;
    opens com.ijad.chatsystem.app.controllers to javafx.fxml;
}