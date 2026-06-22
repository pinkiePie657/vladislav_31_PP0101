package ru.factory.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Optional;

public class ProductionController {
    @FXML private TableView<ProductionItem> productionTable;
    @FXML private TableColumn<ProductionItem, String> orderColumn;
    @FXML private TableColumn<ProductionItem, Integer> quantityColumn;
    @FXML private TableColumn<ProductionItem, String> statusColumn;
    @FXML private TableColumn<ProductionItem, String> progressColumn;

    private ObservableList<ProductionItem> productionData;
    public static class ProductionItem {
        private final SimpleStringProperty order;
        private final SimpleIntegerProperty quantity;
        private final SimpleStringProperty status;
        private final SimpleStringProperty progress;
        public ProductionItem(String order, int qty, String status, String progress) {
            this.order = new SimpleStringProperty(order);
            this.quantity = new SimpleIntegerProperty(qty);
            this.status = new SimpleStringProperty(status);
            this.progress = new SimpleStringProperty(progress);
        }

        public String getOrder() { return order.get(); }
        public int getQuantity() { return quantity.get(); }
        public String getStatus() { return status.get(); }
        public String getProgress() { return progress.get(); }

        public SimpleStringProperty orderProperty() { return order; }
        public SimpleIntegerProperty quantityProperty() { return quantity; }
        public SimpleStringProperty statusProperty() { return status; }
        public SimpleStringProperty progressProperty() { return progress; }
    }
    @FXML
    public void initialize() {
        orderColumn.setCellValueFactory(cell -> cell.getValue().orderProperty());
        quantityColumn.setCellValueFactory(cell -> cell.getValue().quantityProperty().asObject());
        statusColumn.setCellValueFactory(cell -> cell.getValue().statusProperty());
        progressColumn.setCellValueFactory(cell -> cell.getValue().progressProperty());

        productionData = FXCollections.observableArrayList(
                new ProductionItem("Партия красного кирпича #124", 12000, "Обжиг", "75%"),
                new ProductionItem("Силикатный кирпич #125", 8000, "Охлаждение", "40%"),
                new ProductionItem("Облицовочный #126", 5000, "Готово", "100%")
        );
        productionTable.setItems(productionData);
    }
    @FXML
    private void onNewProductionClick() {
        TextInputDialog nameDialog = new TextInputDialog("Новая партия #" + (productionData.size() + 130));
        nameDialog.setTitle("Новый заказ");
        nameDialog.setHeaderText("Создание производственного заказа");
        nameDialog.setContentText("Название партии:");
        Optional<String> nameOpt = nameDialog.showAndWait();
        if (nameOpt.isEmpty() || nameOpt.get().trim().isEmpty()) return;

        TextInputDialog qtyDialog = new TextInputDialog("10000");
        qtyDialog.setTitle("Количество");
        qtyDialog.setHeaderText("Введите количество кирпичей");
        qtyDialog.setContentText("Количество (шт):");
        Optional<String> qtyOpt = qtyDialog.showAndWait();
        if (qtyOpt.isEmpty()) return;
        try {
            int qty = Integer.parseInt(qtyOpt.get().trim());
            if (qty <= 0) throw new NumberFormatException();

            ChoiceDialog<String> statusDialog = new ChoiceDialog<>("Подготовка", "Подготовка", "Формовка", "Сушка", "Обжиг", "Охлаждение", "Готово");
            statusDialog.setTitle("Статус");
            statusDialog.setHeaderText("Начальный статус");
            Optional<String> statusOpt = statusDialog.showAndWait();
            String status = statusOpt.orElse("Подготовка");

            ProductionItem newItem = new ProductionItem(nameOpt.get().trim(), qty, status, "0%");
            productionData.add(newItem);
            new Alert(Alert.AlertType.INFORMATION, "Новый производственный заказ создан!").showAndWait();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Количество должно быть положительным числом!").showAndWait();
        }
    }
}