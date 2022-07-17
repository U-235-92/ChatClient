package aq.koptev.chat.models;

import aq.koptev.chat.controllers.ChatController;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatConnector {

    public static final String AUTHENTICATION_COMMAND = "#l";
    public static final String REGISTRATION_COMMAND = "#r";
    public static final String COMMON_MESSAGE_COMMAND = "#a";
    public static final String PRIVATE_MESSAGE_COMMAND = "#p";
    public static final String ERROR_AUTHENTICATION_COMMAND = "#errauth";
    public static final String OK_AUTHENTICATION_COMMAND = "#okauth";
    public static final String USER_CONNECT_COMMAND = "#c";
    public static final String USER_DISCONNECT_COMMAND = "#dc";
    public static final String PRIVATE_SERVER_MESSAGE = "#psm";
    public static final String CONNECTED_USERS_REQUEST = "#reqcu";
    public static final String COMMON_CHAT = "Общий чат";
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9000;
    private ChatController chatController;
    private int port;
    private String host;
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private String login;

    public ChatConnector() throws IOException {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public ChatConnector(String host, int port) throws IOException{
        this.host = host;
        this.port = port;
        socket = new Socket(host, port);
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
    }

    public void waitChatMessages() {
        Thread thread = new Thread(() -> {
            try {
                waitMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void waitMessage() throws IOException {
        while(true) {
            String message = inputStream.readUTF();
            String command = message.split("\\s+", 2)[0];
            switch (command) {
                case COMMON_MESSAGE_COMMAND:
                case PRIVATE_MESSAGE_COMMAND:
                    sendUserMessageToController(message);
                    break;
                case PRIVATE_SERVER_MESSAGE:
                case USER_CONNECT_COMMAND:
                case USER_DISCONNECT_COMMAND:
                    sendServerMessageToController(message);
                    break;
                case CONNECTED_USERS_REQUEST:
                    String[] users = getConnectedUsers(message.split("\\s+", 2)[1]);
                    Platform.runLater(() -> chatController.setUpConnectedUsers(users));
                    break;
                case OK_AUTHENTICATION_COMMAND:
                    this.login = message.split("\\s+", 2)[1];
                    Platform.runLater(() -> chatController.setUpUserLogin());
                    break;
            }
        }
    }

    private void sendUserMessageToController(String message) {
        String sender = message.split("\\s+", 3)[1];
        String textMessage = message.split("\\s+", 3)[2];
        Platform.runLater(() -> chatController.addMessage(String.format("%s %s", sender, textMessage)));
    }

    private  void sendServerMessageToController(String message) {
        String textMessage = message.split("\\s+", 2)[1];
        Platform.runLater(() -> chatController.addMessage(textMessage));
    }

    private String[] getConnectedUsers(String users) {
        return users.split("\\s+");
    }

    public void sendMessage(String message) throws IOException {
        outputStream.writeUTF(message);
    }

    public String sendAuthMessage(String login, String password) throws IOException {
        outputStream.writeUTF(String.format("%s %s %s", AUTHENTICATION_COMMAND, login, password));
        String answer = inputStream.readUTF();
        if(answer.startsWith(OK_AUTHENTICATION_COMMAND)) {
            return null;
        } else {
            return answer.split("\\s+", 2)[1];
        }
    }

    public String getLogin() {
        return login;
    }

    public void setChatController(ChatController controller) {
        this.chatController = controller;
    }
}
