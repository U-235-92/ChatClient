package aq.koptev.chat.models;

import aq.koptev.chat.controllers.Controllable;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

public class ClientNetwork extends Network {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ClientNetwork() {
        super();
    }

    public ClientNetwork(String host, int port) {
        super(host, port);
    }

    @Override
    protected void connect() {
        try {
            socket = new Socket(getHost(), getPort());
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Ошибка соединения с сервером");
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void acceptMessage(Controllable controller) {
        Thread thread = new Thread(() -> {
            try {
                while(true) {
                    String message = inputStream.readUTF();
                    Platform.runLater(() -> controller.acceptMessage(message));
                }
            } catch (IOException e) {
                System.out.println("Ошибка во время приема сообщения");
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
