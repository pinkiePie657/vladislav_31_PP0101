package ru.factory.service;
public class NotificationService extends Thread {
    @Override
    public void run() {
        while (true) {
            System.out.println("Служба мониторинга: Проверка запасов...");
            try {
                Thread.sleep(60000); // Раз в минуту
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}