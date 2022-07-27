package aq.koptev.chat.controllers;

import aq.koptev.chat.ClientApp;
import aq.koptev.chat.models.Command;
import aq.koptev.chat.models.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.IOException;


public class AuthController implements Observer {

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
    private Observable connector;
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
                connector.sendMessage(Command.AUTHENTICATION_COMMAND, String.format("%s %s", login, password));
            }
        });

        registerBtn.setOnAction((event) -> {
            String login = registerLoginField.getText();
            String password = registerPasswordField.getText();
            if(login.length() == 0) {
                regMessageLabel.setText("Поле логин не может быть пустым");
            } else {
                connector.sendMessage(Command.AUTHENTICATION_COMMAND, String.format("%s %s", login, password));
            }
        });
    }

    @Override
    public synchronized void update(Command command, String message) {
        switch (command) {
            case OK_AUTHENTICATION_COMMAND:
                processSuccessAuthentication(message);
                break;
            case ERROR_AUTHENTICATION_COMMAND:
                processErrorAuthentication(message);
                break;
            case OK_REGISTRATION_COMMAND:
                processSuccessRegistration(message);
                break;
            case ERROR_REGISTRATION_COMMAND:
                processErrorRegistration(message);
                break;
        }
    }

    private void processSuccessAuthentication(String message) {
//        String login = "";
//        String password = "";
//        if(message.split("\\s+", 2).length > 1) {
//            login = message.split("\\s+", 2)[0];
//            password = message.split("\\s+", 2)[1];
//        } else {
//            login = message.split("\\s+", 2)[0];
//        }
//        connector.sendMessage(Command.GET_CONNECTED_USER_COMMAND, String.format("%s %s", login, password));
//        connector.sendMessage(Command.USER_CONNECT_COMMAND, String.format("Пользователь %s вошел в чат", login));
//        connector.sendMessage(Command.GET_CONNECTED_USERS_COMMAND, null);
        try {
            clientApp.showChatView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processErrorAuthentication(String message) {
        authMessageLabel.setTextFill(Color.RED);
        authMessageLabel.setText(message);
        authLoginField.clear();
        authPasswordField.clear();
    }

    private void processSuccessRegistration(String message) {
        regMessageLabel.setTextFill(Color.GREEN);
        regMessageLabel.setText(message);
        registerLoginField.clear();
        registerPasswordField.clear();
    }

    private void processErrorRegistration(String message) {
        regMessageLabel.setTextFill(Color.RED);
        regMessageLabel.setText(message);
        registerLoginField.clear();
        registerPasswordField.clear();
    }

    public void setConnector(Observable connector) {
        this.connector = connector;
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }
}
