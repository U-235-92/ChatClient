package aq.koptev.chat.models;

import aq.koptev.chat.controllers.Observer;

public interface Observable {

    void registerObserver(Observer obs);
    void removeObserver(Observer obs);
    void sendMessage(Command command, String message);
}
