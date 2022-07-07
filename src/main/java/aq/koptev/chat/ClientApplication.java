package aq.koptev.chat;

import aq.koptev.chat.controllers.ClientController;
import aq.koptev.chat.models.ClientNetwork;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.show();

        ClientController controller = fxmlLoader.getController();
        ClientNetwork network = new ClientNetwork(controller);
        controller.setNetwork(network);
        network.acceptMessage();
    }

    public static void main(String[] args) {
        launch();
    }
}