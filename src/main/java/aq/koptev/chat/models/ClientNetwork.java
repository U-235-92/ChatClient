package aq.koptev.chat.models;

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

    private String host;
    private int port;

    public ClientNetwork() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public ClientNetwork(String host, int port) {
        this.host = host;
        this.port = port;
        run();
    }

    private void run() {
        try {
            socket = new Socket(host, port);
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            outputStreamWriter.write(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void acceptMessage() {

    }
}
