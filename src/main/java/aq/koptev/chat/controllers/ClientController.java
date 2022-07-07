package aq.koptev.chat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientController {
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

    private final String FORMAT_DATE_MESSAGE = "dd-MM-yyyy HH:mm";

    public ClientController() {

    }

    @FXML
    void initialize() {
        setWrapListViewTextMessage();
        addActionListeners();
    }

    private void addActionListeners() {
        sendButton.setOnAction((event) -> sendMessage());
        rootComponent.setOnKeyPressed((event) -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        if(isEmptyMessageField()) {
            return;
        }
        String textMessage = textMessage();
        String timeMessage = dateMessage();
        String messageToSend = "[" + timeMessage + "] " + textMessage;
        chatHistory.getItems().add(messageToSend);
        chatHistory.scrollTo(chatHistory.getItems().size() - 1);
        clearMessageField();
    }

    private boolean isEmptyMessageField() {
        return messageField.getText().equals("");
    }

    private String textMessage() {
        return messageField.getText();
    }

    private String dateMessage() {
        return new SimpleDateFormat(FORMAT_DATE_MESSAGE).format(new Date());
    }

    private void clearMessageField() {
        messageField.setText("");
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
}