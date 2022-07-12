package aq.koptev.chat.controllers;

import aq.koptev.chat.ClientApp;
import aq.koptev.chat.models.Connectable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class LoginController implements Controllable {

    @FXML
    private Button closeButton;

    @FXML
    private Button enterButton;

    @FXML
    private TextField loginTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private BorderPane rootComponent;

    @FXML
    private Label errorLabel;

    private Connectable network;

    @FXML
    void initialize() {
        addListeners();
    }

    private void addListeners() {
        closeButton.setOnAction((e) -> {
            System.exit(0);
        });
        enterButton.setOnAction((e) -> {
            sendMessage();
            System.out.println(errorLabel.getText());
//            try {
//                ClientApp.setChatScene();
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
        });
    }

    public void sendMessage() {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();
        if(login == null) {
            login = "";
        }
        if(password == null) {
            password = "";
        }
        String message = login + "#" + password;
        network.sendMessage(message);


    }

    @Override
    public void acceptMessage(String message) {
        errorLabel.setText(message);
    }

    @Override
    public void setNetwork(Connectable network) {
        this.network = network;
    }
}
