module aq.koptev.chatclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens aq.koptev.chat to javafx.fxml;
    exports aq.koptev.chat;
    exports aq.koptev.chat.controllers;
    opens aq.koptev.chat.controllers to javafx.fxml;
}