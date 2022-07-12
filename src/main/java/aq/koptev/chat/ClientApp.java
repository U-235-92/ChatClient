package aq.koptev.chat;

import aq.koptev.chat.controllers.Controllable;
import aq.koptev.chat.models.ClientNetwork;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {

    private static Stage stage;
    private static ClientNetwork network;
    @Override
    public void start(Stage stage) throws IOException {
        ClientApp.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.show();
        Controllable loginController = fxmlLoader.getController();
        network = new ClientNetwork();
        loginController.setNetwork(network);
        network.acceptMessage(loginController);
    }

    public static void setChatScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
        Controllable chatController = fxmlLoader.getController();
        chatController.setNetwork(network);
        network.acceptMessage(chatController);
    }

    public static void main(String[] args) {
        launch();
    }
}