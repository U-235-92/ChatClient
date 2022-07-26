package aq.koptev.chat.controllers;

import aq.koptev.chat.models.Command;

public interface Observer {

    void update(Command command, String message);
}
