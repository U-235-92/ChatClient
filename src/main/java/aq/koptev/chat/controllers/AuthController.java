package aq.koptev.chat.controllers;

import aq.koptev.chat.ClientApp;
import aq.koptev.chat.models.ChatConnector;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

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
    private Label authMessageLabel;

    @FXML
    private Label regMessageLabel;

    private ClientApp clientApp;

    private ChatConnector connector;

    private int countdown = 0;
    private boolean isAuthenticationSuccess = false;

    @FXML
    void initialize() {
//        startCountdownToExit(5);
        addActionListeners();
    }

    private void startCountdownToExit(int seconds) {
        countdown = seconds;
        Thread thread = new Thread(() -> {
            while(countdown-- > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(countdown);
                if(isAuthenticationSuccess) {
                    break;
                }
            }
            if(isAuthenticationSuccess) {
                return;
            } else {
                connector.closeConnection();
                System.exit(0);
            }
        });
        thread.start();
    }

    private void addActionListeners() {
        enterBtn.setOnAction((event) -> {
            String login = authLoginField.getText();
            String password = authPasswordField.getText();
            if(login.length() == 0) {
                authMessageLabel.setText("Поле логин не может быть пустым");
            } else {
                try {
                    String answer = connector.sendAuthMessage(login, password);
                    if(answer != null) {
                        authMessageLabel.setText(answer);
                        authLoginField.clear();
                        authPasswordField.clear();
                    } else {
                        isAuthenticationSuccess = true;
                        clientApp.showChatView();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        registerBtn.setOnAction((event) -> {
            regMessageLabel.setTextFill(Color.RED);
            String login = registerLoginField.getText();
            String password = registerPasswordField.getText();
            if(login.length() == 0) {
                regMessageLabel.setText("Поле логин не может быть пустым");
            } else {
                try {
                    String answer = connector.sendRegisterMessage(login, password);
                    if(answer != null) {
                        regMessageLabel.setText(answer);
                    } else {
                        regMessageLabel.setTextFill(Color.GREEN);
                        regMessageLabel.setText("Регистрация успешно завершена");
                        registerLoginField.clear();
                        registerPasswordField.clear();
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
