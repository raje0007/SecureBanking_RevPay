package org.revpay.service;

import org.revpay.dao.NotificationDAO;
import org.revpay.dao.impl.NotificationDAOImpl;
import org.revpay.model.Notification;

import java.util.List;

public class NotificationService {

    private final NotificationDAO notificationDAO =
            new NotificationDAOImpl();

    public void sendNotification(Long userId,
                                 String title,
                                 String message,
                                 String type) {

        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);

        notificationDAO.save(n);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationDAO.findByUserId(userId);
    }

    public void markAsRead(Long id) {
        notificationDAO.markAsRead(id);
    }
}