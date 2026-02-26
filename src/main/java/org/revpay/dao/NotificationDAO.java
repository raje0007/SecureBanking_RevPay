package org.revpay.dao;

import org.revpay.model.Notification;
import java.util.List;

public interface NotificationDAO {

    void save(Notification notification);

    List<Notification> findByUserId(Long userId);

    void markAsRead(Long notificationId);
}