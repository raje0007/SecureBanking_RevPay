package org.revpay.controller;

import org.revpay.model.Notification;
import org.revpay.model.User;
import org.revpay.service.NotificationService;

import java.util.List;
import java.util.Scanner;

public class NotificationController {

    private final NotificationService service =
            new NotificationService();
    private final Scanner scanner = new Scanner(System.in);

    public void showNotifications(User user) {

        List<Notification> list =
                service.getUserNotifications(user.getId());

        if (list.isEmpty()) {
            System.out.println("No notifications.");
            return;
        }

        for (Notification n : list) {

            System.out.println("\nID: " + n.getId());
            System.out.println("Title: " + n.getTitle());
            System.out.println("Message: " + n.getMessage());
            System.out.println("Type: " + n.getType());
            System.out.println("Read: " + n.isRead());
        }

        System.out.print("\nMark any as read? (Enter ID or 0): ");
        Long id = Long.parseLong(scanner.nextLine());

        if (id != 0) {
            service.markAsRead(id);
            System.out.println("Marked as read.");
        }
    }
}