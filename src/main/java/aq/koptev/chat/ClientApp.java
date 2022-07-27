package aq.koptev.chat;

import aq.koptev.chat.controllers.AuthController;
import aq.koptev.chat.controllers.ChatController;
import aq.koptev.chat.controllers.SettingsController;
import aq.koptev.chat.models.ChatConnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {
    private Stage chatStage;
    private Stage authStage;
    private Stage settingsStage;
    private ChatConnector connector;
    private ChatController chatController;
    private AuthController authController;
    private SettingsController settingsController;

    @Override
    public void start(Stage stage) throws IOException {
        chatStage = stage;
        connector = new ChatConnector();

        buildAuthView();
        buildChatView();
        buildSettingsView();
    }

    private void buildAuthView() throws IOException {
        authStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("auth-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        authStage.setTitle("Чат");
        authStage.setScene(scene);
        authStage.show();
        authController = fxmlLoader.getController();
        authController.setConnector(connector);
        authController.setClientApp(this);
    }

    private void buildChatView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        chatStage.setTitle("Чат");
        chatStage.setScene(scene);
        chatController = fxmlLoader.getController();
        chatController.setClientApp(this);
        chatController.setConnector(connector);
    }

    private void buildSettingsView() throws IOException {
        settingsStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("settings-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        settingsStage.setTitle("Настройки");
        settingsStage.setScene(scene);
        settingsController = fxmlLoader.getController();
        settingsController.setClientApp(this);
        settingsController.setConnector(connector);
    }

    public void showChatView() throws IOException {
        closeAuthView();
        chatStage.show();
        connector.waitChatMessages();
        connector.setChatController(chatController);
    }

    private void closeAuthView() {
        authStage.close();
    }

    public void showSettingsView() {
        settingsController.setLogin();
        settingsController.setPassword();
        settingsStage.show();
        connector.setSettingsController(settingsController);
    }

    public void closeSettingsView() {
        settingsStage.close();
    }

    public static void main(String[] args) {
        launch();
    }
}