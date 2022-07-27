package aq.koptev.chat.models;

import aq.koptev.chat.controllers.ChatController;
import aq.koptev.chat.controllers.SettingsController;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatConnector {

    public static final String COMMON_CHAT = "Общий чат";
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9000;
    private ChatController chatController;
    private SettingsController settingsController;
    private int port;
    private String host;
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
//    private String login;

    private User user;

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
            if(command.equals(Command.COMMON_MESSAGE_COMMAND.getCommand()) ||
                        command.equals(Command.PRIVATE_USER_MESSAGE_COMMAND.getCommand())) {
                sendUserMessageToController(message);
            } else if(command.equals(Command.PRIVATE_SERVER_MESSAGE.getCommand()) ||
                        command.equals(Command.USER_CONNECT_COMMAND.getCommand()) ||
                        command.equals(Command.USER_DISCONNECT_COMMAND.getCommand())) {
                sendServerMessageToController(message);
            } else if(command.equals(Command.GET_CONNECTED_USERS_COMMAND.getCommand())) {
                String[] users = getConnectedUsers(message.split("\\s+", 2)[1]);
                Platform.runLater(() -> chatController.setUpConnectedUsers(users));
            } else if(command.equals(Command.GET_CONNECTED_USER_COMMAND.getCommand())) {
                String login = "";
                String password = "";
                if(message.split("\\s+", 3).length == 2) {
                    login = message.split("\\s+", 3)[1];
                } else {
                    login = message.split("\\s+", 3)[1];
                    password = message.split("\\s+", 3)[2];
                }
                user = new User(login, password);
                Platform.runLater(() -> chatController.setUserLogin(user.getLogin()));
            } else if(command.equals(Command.OK_CHANGE_USER_ACCOUNT_SETTINGS_COMMAND.getCommand())) {
                String login;
                String password;
                if(message.split("\\s+", 2).length > 1) {
                    login = message.split("\\s+", 2)[0];
                    password = message.split("\\s+", 2)[1];
                    user.setLogin(login);
                    user.setPassword(password);
                } else {
                    login = message.split("\\s+", 2)[0];
                    password = "";
                    user.setLogin(login);
                    user.setPassword(password);
                }
                Platform.runLater(() -> chatController.setUserLogin(login));
                Platform.runLater(() -> settingsController.printOkChangeLoginMessage("Успешно"));
            } else if(command.equals(Command.ERROR_CHANGE_USER_ACCOUNT_SETTINGS_COMMAND.getCommand())) {
                String errorMessage = message.split("\\s+", 2)[1];
                Platform.runLater(() -> settingsController.printErrorChangeLoginMessage(errorMessage));
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
        outputStream.writeUTF(String.format("%s %s %s", Command.AUTHENTICATION_COMMAND.getCommand(), login, password));
        String answer = inputStream.readUTF();
        if(answer.startsWith(Command.OK_AUTHENTICATION_COMMAND.getCommand())) {
            outputStream.writeUTF(String.format("%s Пользователь %s вошел в чат", Command.USER_CONNECT_COMMAND.getCommand(), login));
            return null;
        } else {
            return answer.split("\\s+", 2)[1];
        }
    }

    public String sendRegisterMessage(String login, String password) throws IOException {
        outputStream.writeUTF(String.format("%s %s %s", Command.REGISTRATION_COMMAND.getCommand(), login, password));
        String answer = inputStream.readUTF();
        if(answer.startsWith(Command.OK_REGISTRATION_COMMAND.getCommand())) {
            return null;
        } else {
            return answer.split("\\s+", 2)[1];
        }
    }

    public void setChatController(ChatController controller) {
        this.chatController = controller;
    }

    public void setSettingsController(SettingsController controller) {
        this.settingsController = controller;
    }

    public User getUser() {
        return user;
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
