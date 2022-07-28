package aq.koptev.chat.controllers;

import aq.koptev.chat.ClientApp;
import aq.koptev.chat.models.Command;
import aq.koptev.chat.models.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatController implements Observer {

    private final String COMMON_CHAT = "Общий чат";
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
    private Button settingsButton;
    @FXML
    private Label loginLabel;
    private Observable connector;
    private String selectedReceiver;
    private ClientApp clientApp;

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
        settingsButton.setOnAction((event) -> {
            clientApp.showSettingsView();
        });
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
        String sender = loginLabel.getText();
        String messageToSend;
        if(selectedReceiver == null || selectedReceiver.equals(COMMON_CHAT)) {
            messageToSend = String.format("%s %s", sender, textMessage);
            connector.sendMessage(Command.COMMON_MESSAGE_COMMAND, messageToSend);
        } else {
            messageToSend = String.format("%s %s %s", sender, selectedReceiver, textMessage);
            connector.sendMessage(Command.PRIVATE_MESSAGE_COMMAND, messageToSend);
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

    @Override
    public synchronized void update(Command command, String message) {
        switch (command) {
            case COMMON_MESSAGE_COMMAND:
            case PRIVATE_MESSAGE_COMMAND:
                processClientMessage(message);
                break;
            case CLIENT_CONNECT_COMMAND:
                processClientConnect(message);
                break;
            case CLIENT_DISCONNECT_COMMAND:
                processClientDisconnect(message);
            case ANSWER_CONNECTED_CLIENTS_COMMAND:
                processConnectionClients(message);
                break;
            case ANSWER_CONNECTED_CLIENT_COMMAND:
                processConnectionClient(message);
                break;
        }
    }

    private void processClientMessage(String message) {
        String sender = message.split("\\s+", 2)[0];
        String textMessage = message.split("\\s+", 2)[1];
        addMessage(String.format("%s %s", sender, textMessage));
    }

    private void processClientConnect(String message) {
        addMessage(message);
        connector.sendMessage(Command.REQUEST_CONNECTED_CLIENTS_COMMAND, "");
    }

    private void processClientDisconnect(String message) {
        addMessage(message);
        connector.sendMessage(Command.REQUEST_CONNECTED_CLIENTS_COMMAND, "");
    }

    private void addMessage(String message) {
        chatHistory.getItems().add(message);
        chatHistory.scrollTo(chatHistory.getItems().size() - 1);
    }

    private void processConnectionClients(String message) {
        String[] users = message.split("\\s+");
        addConnectedUsers(users);
    }

    private void addConnectedUsers(String[] users) {
        if(users != null) {
            if(this.users.getItems().size() > 0) {
                this.users.getItems().removeAll(this.users.getItems());
            }
            this.users.getItems().add(COMMON_CHAT);
            this.users.getItems().addAll(users);
        }
    }

    private void processConnectionClient(String message) {
        String login = "";
        String password = "";
        if(message.split("\\s+", 2).length > 1) {
            login = message.split("\\s+", 2)[0];
            password = message.split("\\s+", 2)[1];
        } else {
            login = message.split("\\s+", 2)[0];
        }
        setUserLogin(login);
        connector.sendMessage(Command.NOTIFY_CLIENTS_ON_CLIENT_CONNECT_COMMAND, String.format("Пользователь %s вошел в чат", login));
    }

    private void setUserLogin(String login) {
        loginLabel.setText(login);
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    public void setConnector(Observable connector) {
        this.connector = connector;
    }
}