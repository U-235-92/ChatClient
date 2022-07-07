package aq.koptev.chat;

import aq.koptev.chat.controllers.ClientController;
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
    }

    public static void main(String[] args) {
        launch();
    }
}