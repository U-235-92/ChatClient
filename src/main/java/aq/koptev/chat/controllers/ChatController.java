package aq.koptev.chat.controllers;

import aq.koptev.chat.models.ChatConnector;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatController {

    private final String FORMAT_DATE_MESSAGE = "dd-MM-yyyy HH:mm";
    @FXML
    private ListView<String> chatHistory;
    @FXML
    private ListView<String> users;
    @FXML
    private TextField messageField;
    @FXML
    private BorderPane rootComponent;
    @FXML
    private Button sendButton;
    @FXML
    private Label loginTextField;
    private ChatConnector connector;
    private String selectedReceiver;

    public ChatController() {}

    @FXML
    void initialize() {
        setWrapListViewTextMessage();
        setUserListViewSelectActivity();
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
                            text.setWrappingWidth(chatHistory.getWidth());
                            setGraphic(text);
                        }
                    }
                };
                return cell;
            }
        });
    }

    private void setUserListViewSelectActivity() {

        users.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = users.getSelectionModel();
            ListCell<String> cells = new ListCell<>();
            cells.textProperty().bind(cells.itemProperty());
            cells.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                users.requestFocus();
                if(!cells.isEmpty()) {
                    int index = cells.getIndex();
                    if(selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedReceiver = null;
                    } else {
                        selectionModel.select(index);
                        selectedReceiver = cells.getItem();
                    }
                    event.consume();
                }
            });
            return cells;
        });
    }

    private void addActionListeners() {
        sendButton.setOnAction((event) -> writeMessage());
        rootComponent.setOnKeyPressed((event) -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                writeMessage();
            }
        });
    }

    private void writeMessage() {
        if(isEmptyMessageField()) {
            return;
        }
        String textMessage = textMessage();
        String sender = connector.getLogin();
        String messageToSend;
        if(selectedReceiver == null || selectedReceiver.equals(ChatConnector.COMMON_CHAT)) {
            messageToSend = String.format("%s %s %s", ChatConnector.COMMON_MESSAGE_COMMAND, sender, textMessage);
        } else {
            messageToSend = String.format("%s %s %s %s", ChatConnector.PRIVATE_MESSAGE_COMMAND,
                    sender, selectedReceiver, textMessage);
        }
        try {
            connector.sendMessage(messageToSend);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clearMessageField();
    }

    private boolean isEmptyMessageField() {
        return messageField.getText().equals("");
    }

    private String textMessage() {
        return String.format("[%s] %s", dateMessage(), messageField.getText().trim());
    }

    private String dateMessage() {
        return new SimpleDateFormat(FORMAT_DATE_MESSAGE).format(new Date());
    }

    private void clearMessageField() {
        messageField.setText("");
    }

    public void addMessage(String message) {
        chatHistory.getItems().add(message);
        chatHistory.scrollTo(chatHistory.getItems().size() - 1);
    }


    public void setConnector(ChatConnector connector) {
        this.connector = connector;
    }

    public void setUpConnectedUsers(String[] users) {
        if(users != null) {
            if(this.users.getItems().size() > 0) {
                this.users.getItems().removeAll(this.users.getItems());
            }
            this.users.getItems().add(ChatConnector.COMMON_CHAT);
            this.users.getItems().addAll(users);
        }
    }

    public void setUpUserLogin() {
        loginTextField.setText(connector.getLogin());
    }
}