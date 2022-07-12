package aq.koptev.chat.controllers;

import aq.koptev.chat.models.Connectable;

public interface Controllable {

    void acceptMessage(String message);

    void setNetwork(Connectable connectable);
}
