package ru.factory.dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.factory.model.Brick;
public class BrickDAO {
    public ObservableList<Brick> getAllBricks() {
        ObservableList<Brick> list = FXCollections.observableArrayList();
        return list;
    }
}