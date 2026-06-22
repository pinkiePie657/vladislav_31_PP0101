package ru.factory.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import ru.factory.dao.DatabaseHandler;
import ru.factory.model.Brick;
import ru.factory.model.CurrentUser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class InventoryController {
    @FXML private TableView<Brick> inventoryTable;
    @FXML private TableColumn<Brick, String> nameColumn;
    @FXML private TableColumn<Brick, Integer> quantityColumn;
    @FXML private TableColumn<Brick, String> statusColumn;
    @FXML private TextField searchField;
    private ObservableList<Brick> data;
    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        loadBricksFromDB();

        inventoryTable.setRowFactory(tv -> new TableRow<Brick>() {
            @Override
            protected void updateItem(Brick item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if ("Нет на складе".equals(item.getStatus())) {
                    setStyle("-fx-background-color: #FFEBEE;");
                } else if ("Мало".equals(item.getStatus())) {
                    setStyle("-fx-background-color: #FFF8E1;");
                } else {
                    setStyle("");
                }
            }
        });
    }
    private void loadBricksFromDB() {
        data = DatabaseHandler.getAllBricks();
        inventoryTable.setItems(data);
    }
    @FXML
    private void onSearchClick() {
        String text = searchField.getText();
        if (text == null || text.trim().isEmpty()) {
            inventoryTable.setItems(data);
        } else {
            String lower = text.toLowerCase().trim();
            inventoryTable.setItems(data.filtered(brick ->
                    brick.getName().toLowerCase().contains(lower)));
        }
    }
    @FXML
    private void onResetSearchClick() {
        searchField.clear();
        inventoryTable.setItems(data);
    }
    @FXML
    private void onIncomingClick() {
        if (CurrentUser.getUser() == null) {
            new Alert(Alert.AlertType.WARNING, "Для поступления необходимо авторизоваться!").showAndWait();
            return;
        }
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Поступление");
        nameDialog.setHeaderText("Новое поступление на склад");
        nameDialog.setContentText("Наименование кирпича:");
        Optional<String> nameOpt = nameDialog.showAndWait();
        if (nameOpt.isEmpty() || nameOpt.get().trim().isEmpty()) return;

        TextInputDialog qtyDialog = new TextInputDialog("100");
        qtyDialog.setTitle("Количество");
        qtyDialog.setHeaderText("Введите количество");
        qtyDialog.setContentText("Количество (шт):");
        Optional<String> qtyOpt = qtyDialog.showAndWait();
        if (qtyOpt.isPresent()) {
            try {
                int qty = Integer.parseInt(qtyOpt.get().trim());
                if (qty <= 0) throw new NumberFormatException();

                DatabaseHandler.addBrick(nameOpt.get().trim(), qty);
                loadBricksFromDB();

                new Alert(Alert.AlertType.INFORMATION, "Поступление успешно добавлено!").showAndWait();
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Количество должно быть положительным числом!").showAndWait();
            }
        }
    }
    @FXML
    private void onOutgoingClick() {
        if (CurrentUser.getUser() == null) {
            new Alert(Alert.AlertType.WARNING, "Для отгрузки необходимо авторизоваться!").showAndWait();
            return;
        }
        if (data == null || data.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "На складе нет товаров для отгрузки.").showAndWait();
            return;
        }
        ChoiceDialog<Brick> dialog = new ChoiceDialog<>(data.get(0), data);
        dialog.setTitle("Отгрузка со склада");
        dialog.setHeaderText("Шаг 1 из 2: Выберите кирпич");
        dialog.setContentText("Какой кирпич нужно отгрузить?");

        Optional<Brick> selectedOpt = dialog.showAndWait();
        if (selectedOpt.isEmpty()) return;
        Brick brick = selectedOpt.get();
        TextInputDialog qtyDialog = new TextInputDialog("100");
        qtyDialog.setTitle("Отгрузка со склада");
        qtyDialog.setHeaderText("Шаг 2 из 2: Введите количество");
        qtyDialog.setContentText("Сколько штук отгрузить?\n(Доступно: " + brick.getQuantity() + " шт)");

        Optional<String> qtyOpt = qtyDialog.showAndWait();
        if (qtyOpt.isPresent()) {
            try {
                int qty = Integer.parseInt(qtyOpt.get().trim());
                if (qty <= 0 || qty > brick.getQuantity()) {
                    new Alert(Alert.AlertType.ERROR,
                            "Неверное количество!\nДолжно быть от 1 до " + brick.getQuantity() + " шт.").showAndWait();
                    return;
                }
                int newQty = brick.getQuantity() - qty;
                DatabaseHandler.updateBrickQuantity(brick.getName(), newQty);
                loadBricksFromDB();
                new Alert(Alert.AlertType.INFORMATION,
                        "Отгрузка выполнена!\n\n" + brick.getName() + "\nОтгружено: " + qty + " шт").showAndWait();

            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Введите целое число!").showAndWait();
            }
        }
    }
    @FXML
    private void onExportClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Экспорт склада в CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV файлы", "*.csv"));
        fileChooser.setInitialFileName("inventory_export.csv");
        File file = fileChooser.showSaveDialog(inventoryTable.getScene().getWindow());
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Наименование;Количество;Статус\n");
                for (Brick brick : data) {
                    writer.write(String.format("%s;%d;%s\n", brick.getName(), brick.getQuantity(), brick.getStatus()));
                }
                new Alert(Alert.AlertType.INFORMATION,
                        "Экспорт выполнен успешно!\nФайл: " + file.getAbsolutePath()).showAndWait();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Ошибка при экспорте!").showAndWait();
            }
        }
    }
}