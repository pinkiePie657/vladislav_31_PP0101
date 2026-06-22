package ru.factory.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import ru.factory.dao.DatabaseHandler;
import ru.factory.model.CurrentUser;
import ru.factory.service.ChatService;

import java.util.Collections;
import java.util.List;

public class ChatController {
    @FXML private ListView<String> chatListView;
    @FXML private TextField messageInput;
    private final ChatService chatService = new ChatService();
    private Timeline chatTimeline;
    @FXML
    public void initialize() {
        loadMessages();
        setupChatUpdater();
    }
    private void loadMessages() {
        List<String> msgs = DatabaseHandler.getMessages();
        Collections.reverse(msgs);
        chatListView.setItems(FXCollections.observableArrayList(msgs));
        if (!msgs.isEmpty()) {
            chatListView.scrollTo(msgs.size() - 1);
        }
    }
    private void setupChatUpdater() {
        chatTimeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> loadMessages()));
        chatTimeline.setCycleCount(Animation.INDEFINITE);
        chatTimeline.play();
    }
    @FXML
    private void sendMessage() {
        if (CurrentUser.getUser() == null) {
            new Alert(Alert.AlertType.WARNING, "Для отправки сообщений необходимо авторизоваться!").showAndWait();
            return;
        }
        String text = messageInput.getText();
        if (text != null && !text.trim().isEmpty()) {
            chatService.sendMessage(CurrentUser.getUser().getId(), text.trim());
            messageInput.clear();
            loadMessages();
            messageInput.requestFocus();
        }
    }
}