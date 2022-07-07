package aq.koptev.chat.models;

import aq.koptev.chat.controllers.ClientController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientNetwork {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    private Socket socket;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private OutputStreamWriter outputStreamWriter;

    private ClientController controller;

    private String host;
    private int port;

    public ClientNetwork(ClientController controller) {
        this(DEFAULT_HOST, DEFAULT_PORT, controller);
    }

    public ClientNetwork(String host, int port, ClientController controller) {
        this.host = host;
        this.port = port;
        this.controller = controller;
        connect();
    }

    private void connect() {
        try {
            socket = new Socket(host, port);
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Ошибка соединения с сервером");
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            outputStreamWriter.write(message);
        } catch (IOException e) {
            System.out.println("Ошибка во время отправки сообщения");
            throw new RuntimeException(e);
        }
    }

    public void acceptMessage() {
        Runnable task = () -> {
            String message = null;
            try {
                while (true) {
                    if((message = bufferedReader.readLine()) != null) {
                        controller.acceptMessage(message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка во время приема сообщения");
                e.printStackTrace();
            }
        };
        Thread messageWaiter = new Thread(task);
        messageWaiter.setDaemon(true);
        messageWaiter.start();
    }
}
