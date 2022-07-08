package aq.koptev.chat.models;

import aq.koptev.chat.controllers.Controllable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientNetwork extends Network {

    private Socket socket;
    private InputStreamReader inputStream;
    private BufferedReader bufferedReader;
    private OutputStreamWriter outputStream;

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
            inputStream = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStream);
            outputStream = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Ошибка соединения с сервером");
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        try {
            outputStream.write(message);
        } catch (IOException e) {
            System.out.println("Ошибка во время отправки сообщения");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void acceptMessage(Controllable controller) {
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
