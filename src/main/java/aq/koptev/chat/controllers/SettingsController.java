package aq.koptev.chat.controllers;

import aq.koptev.chat.ClientApp;
import aq.koptev.chat.models.ChatConnector;
import aq.koptev.chat.models.Command;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.IOException;

public class SettingsController {

    @FXML
    private Button cancelButton;
    @FXML
    private TextField loginField;
    @FXML
    private Label messageLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button saveButton;
    private ClientApp clientApp;
    private ChatConnector connector;

    @FXML
    void initialize() {
        addActionListeners();
    }

    private void addActionListeners() {
        cancelButton.setOnAction((event) -> {
            clearText();
            clientApp.closeSettingsView();
        });

        saveButton.setOnAction((event) -> {
            String oldLogin = connector.getUser().getLogin();
            String newLogin = loginField.getText().trim();
            String oldPassword = connector.getUser().getPassword();
            String newPassword = passwordField.getText().trim();
            if(newLogin.equals("")) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Поле логин не может быть пустым");
            } else if(newLogin.matches("\\s+")) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Поле логин не может содержать пробел");
            } else if(newLogin.length() > 30 || newPassword.length() > 30) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Логин и пароль не могут быть больше 30 символов");
            } else {
                try {
                    connector.sendMessage(String.format("%s %s %s %s %s",
                            Command.CHANGE_USER_ACCOUNT_SETTINGS_COMMAND.getCommand(), oldLogin, newLogin, oldPassword, newPassword));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void clearText() {
        passwordField.clear();
        loginField.clear();
        messageLabel.setText("");
    }

    public void setConnector(ChatConnector connector) {
        this.connector = connector;
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    public void setUpLogin() {
        loginField.setText(connector.getUser().getLogin());
    }

    public void setUpPassword() {
        passwordField.setText(connector.getUser().getPassword());
    }

    public void printErrorChangeLoginMessage(String message) {
        messageLabel.setTextFill(Color.RED);
        messageLabel.setText(message);
    }

    public void printOkChangeLoginMessage(String message) {
        messageLabel.setTextFill(Color.GREEN);
        messageLabel.setText(message);
    }
}
