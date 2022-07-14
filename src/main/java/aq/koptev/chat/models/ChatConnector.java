package aq.koptev.chat.models;

import aq.koptev.chat.controllers.ChatController;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatConnector {

    public static final String USERS_SPLITTER = "#";
    public static final String SPACE_SYMBOL = " ";
    public static final String AUTHORIZE_COMMAND = "//log";
    public static final String CONNECT_COMMAND = "//connected";
    public static final String LOGIN_COMMAND = "//login";
    public static final String PERSONAL_MESSAGE_COMMAND = "//personal";
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9000;
    private ChatController controller;
    private int port;
    private String host;
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private String login;

    public ChatConnector(ChatController controller, String host, int port) {
        this.controller = controller;
        this.host = host;
        this.port = port;
    }

    public ChatConnector(ChatController controller) {
        this(controller, DEFAULT_HOST, DEFAULT_PORT);
    }

    public void connectProcess() throws IOException {
        socket = new Socket(host, port);
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
        Thread thread = new Thread(() -> {
            try {
                receiveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void receiveData() throws IOException {
        while(true) {
            String data = inputStream.readUTF();
            if(data.startsWith(CONNECT_COMMAND)) {
                String users = data.substring(CONNECT_COMMAND.length());
                String[] userLogins = users.split(USERS_SPLITTER);
                Platform.runLater(() -> controller.setConnectedUsers(userLogins));
            } else if (data.startsWith(LOGIN_COMMAND)) {
                String login = data.substring(LOGIN_COMMAND.length());
                this.login = login;
                Platform.runLater(() -> controller.setLogin(login));
            } else {
                Platform.runLater(() -> controller.addMessage(data));
            }
        }
    }

    public void sendData(String data) throws IOException {
        outputStream.writeUTF(data);
    }

    public String getLogin() {
        return login;
    }
}
