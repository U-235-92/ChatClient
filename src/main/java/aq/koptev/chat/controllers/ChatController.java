package aq.koptev.chat.controllers;

import aq.koptev.chat.models.ChatConnector;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatController {

    private final String FORMAT_DATE_MESSAGE = "dd-MM-yyyy,HH:mm";
    @FXML
    private ListView<String> chatHistory;
    @FXML
    private ListView<String> chatUsers;
    @FXML
    private TextField messageField;
    @FXML
    private BorderPane rootComponent;
    @FXML
    private Button sendButton;
    @FXML
    private Label loginTextField;
    private ChatConnector connector;

    public ChatController() {}

    @FXML
    void initialize() {
        connector = new ChatConnector(this);
        try {
            connector.connectProcess();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setWrapListViewTextMessage();
        addActionListeners();
    }

    private void setWrapListViewTextMessage() {
        chatHistory.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            public ListCell<String> call(ListView<String> list) {
                final ListCell cell = new ListCell() {
                    private Text text;
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            text = new Text(item.toString());
                            text.setWrappingWidth(chatHistory.getPrefWidth());
                            setGraphic(text);
                        }
                    }
                };
                return cell;
            }
        });
    }

    private void addActionListeners() {
        sendButton.setOnAction((event) -> writeDataStream());
        rootComponent.setOnKeyPressed((event) -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                writeDataStream();
            }
        });
    }

    private void writeDataStream() {
        if(isEmptyMessageField()) {
            return;
        }
        String textMessage = textMessage();
        String dateMessage = dateMessage();
        String login = connector.getLogin();
        try {
            if(textMessage.startsWith(ChatConnector.AUTHORIZE_COMMAND)) {
                connector.sendData(textMessage);
            } else if(textMessage.startsWith(ChatConnector.PERSONAL_MESSAGE_COMMAND)) {
                textMessage = textMessage.substring(ChatConnector.PERSONAL_MESSAGE_COMMAND.length());
                String formattedMessage = getFormattedMessagePersonalCommand(login, dateMessage, textMessage);
                connector.sendData(formattedMessage);
            } else {
                String formattedMessage = getFormattedMessageWithoutCommand(login, dateMessage, textMessage);
                connector.sendData(formattedMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clearMessageField();
    }

    private boolean isEmptyMessageField() {
        return messageField.getText().equals("");
    }

    private String textMessage() {
        return messageField.getText().trim();
    }

    private String dateMessage() {
        return new SimpleDateFormat(FORMAT_DATE_MESSAGE).format(new Date());
    }

    private String getFormattedMessagePersonalCommand(String login, String dateMessage, String textMessage) {
        return ChatConnector.PERSONAL_MESSAGE_COMMAND + ChatConnector.SPACE_SYMBOL +
                getFormattedMessageWithoutCommand(login, dateMessage, textMessage);
    }

    private String getFormattedMessageWithoutCommand(String login, String dateMessage, String textMessage) {
        return login + " [" + dateMessage + "] " + textMessage;
    }

    private void clearMessageField() {
        messageField.setText("");
    }

    public void setConnectedUsers(String[] connectedUsers) {
        chatUsers.getItems().removeAll(chatUsers.getItems());
        for(String user : connectedUsers) {
            chatUsers.getItems().add(user);
        }
    }

    public void setLogin(String login) {
        loginTextField.setText(login);
    }

    public void addMessage(String message) {
        chatHistory.getItems().add(message);
        chatHistory.scrollTo(chatHistory.getItems().size() - 1);
    }
}