package ru.factory.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import ru.factory.dao.DatabaseHandler;
import java.util.List;
public class ChatService extends Service<List<String>> {
    @Override
    protected Task<List<String>> createTask() {
        return new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                while (!isCancelled()) {
                    List<String> messages = DatabaseHandler.getMessages();
                    updateValue(messages);
                    Thread.sleep(3000);
                }
                return null;
            }
        };
    }
    public void sendMessage(int senderId, String text) {
        if (senderId > 0 && text != null && !text.trim().isEmpty()) {
            DatabaseHandler.saveMessage(senderId, text.trim());
        }
    }
}