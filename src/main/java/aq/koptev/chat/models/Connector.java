package aq.koptev.chat.models;

import aq.koptev.chat.controllers.Observer;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Connector implements Observable {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5082;
    private List<Observer> observers;
    private int port;
    private String host;
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private Command command = null;
    private String message = null;

    public Connector() throws IOException {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public Connector(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        socket = new Socket(host, port);
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
        observers = new ArrayList<>();
        waitMessage();
    }

    private synchronized void waitMessage() {
        Thread thread = new Thread(() -> {
            while(true) {
                String incoming;
                try {
                    incoming = inputStream.readUTF();
                    if(incoming.split("\\s+", 2).length > 1) {
                        command = Command.getCommandByValue(incoming.split("\\s+", 2)[0]);
                        message = incoming.split("\\s+", 2)[1];
                    } else {
                        command = Command.getCommandByValue(incoming.split("\\s+", 2)[0]);
                        message = "";
                    }
                    System.out.println(incoming);
                    notifyObservers();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void notifyObservers() {
        for(Observer obs : observers) {
            Platform.runLater(() -> obs.update(command, message));
        }
    }

    @Override
    public synchronized void registerObserver(Observer obs) {
        observers.add(obs);
    }

    @Override
    public synchronized void removeObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void sendMessage(Command command, String message) {
        String outputMessage = String.format("%s %s", command.getCommand(), message);
        try {
            outputStream.writeUTF(outputMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
