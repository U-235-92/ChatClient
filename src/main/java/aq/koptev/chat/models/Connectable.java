package aq.koptev.chat.models;

import aq.koptev.chat.controllers.Controllable;

public interface Connectable {
    void sendMessage(String message);
    void acceptMessage(Controllable controller);

    String getHost();

    int getPort();
}
