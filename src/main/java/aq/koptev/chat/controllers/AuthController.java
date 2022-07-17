package aq.koptev.chat.controllers;

import aq.koptev.chat.ClientApp;
import aq.koptev.chat.models.ChatConnector;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;


public class AuthController {

    @FXML
    private TextField authLoginField;

    @FXML
    private PasswordField authPasswordField;

    @FXML
    private Button enterBtn;

    @FXML
    private Button registerBtn;

    @FXML
    private TextField registerLoginField;

    @FXML
    private PasswordField registerPasswordField;

    @FXML
    private TabPane rootComponent;

    @FXML
    private Tab tabEnter;

    @FXML
    private Tab tabRegister;

    @FXML
    private Label errLabel;

    private ClientApp clientApp;

    private ChatConnector connector;

    @FXML
    void initialize() {
        addActionListeners();
    }

    private void addActionListeners() {
        enterBtn.setOnAction((event) -> {
            String login = authLoginField.getText();
            String password = authPasswordField.getText();
            if(login.length() == 0) {
                errLabel.setText("Поле логин не может быть пустым");
            } else {
                try {
                    String answer = connector.sendAuthMessage(login, password);
                    if(answer != null) {
                        errLabel.setText(answer);
                    } else {
                        clientApp.showChatView();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setConnector(ChatConnector connector) {
        this.connector = connector;
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }
}
