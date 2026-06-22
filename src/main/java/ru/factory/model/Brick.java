package ru.factory.model;

import javafx.beans.property.*;

public class Brick {
    private final StringProperty name;
    private final IntegerProperty quantity;
    private final StringProperty status;

    public Brick(String name, int quantity, String status) {
        this.name = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.status = new SimpleStringProperty(status);
    }
    public String getName() { return name.get(); }
    public int getQuantity() { return quantity.get(); }
    public String getStatus() { return status.get(); }

    public StringProperty nameProperty() { return name; }
    public IntegerProperty quantityProperty() { return quantity; }
    public StringProperty statusProperty() { return status; }

    @Override
    public String toString() {
        return getName() + " (" + getQuantity() + " шт, " + getStatus() + ")";
    }
}