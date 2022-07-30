package aq.koptev.chat.controllers;

import aq.koptev.chat.models.Command;

public interface Observer {

    void update(Command command, String message);
    default void readChatHistory() { throw new UnsupportedOperationException(); }
    default void writeChatHistory() { throw new UnsupportedOperationException(); }
}
