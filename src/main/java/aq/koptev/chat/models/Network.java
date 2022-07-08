package aq.koptev.chat.models;

public abstract class Network implements Connectable {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    private String host;
    private int port;

    public Network() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public Network(String host, int port) {
        this.host = host;
        this.port = port;
        connect();
    }

    protected abstract void connect();

    @Override
    public final String getHost() {
        return host;
    }

    @Override
    public final int getPort() {
        return port;
    }
}
