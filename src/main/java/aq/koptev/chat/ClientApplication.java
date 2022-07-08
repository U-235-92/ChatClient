package aq.koptev.chat;

import aq.koptev.chat.controllers.ClientController;
import aq.koptev.chat.controllers.Controllable;
import aq.koptev.chat.models.ClientNetwork;
import aq.koptev.chat.models.Connectable;
import aq.koptev.chat.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.show();

//        Connectable network = new ClientNetwork();
//        Controllable controller = fxmlLoader.getController();
//        controller.setNetwork(network);
//        network.acceptMessage(controller);
    }

    public static void main(String[] args) {
        launch();
    }
}