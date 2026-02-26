package org.revpay.dao.impl;

import org.revpay.config.DBConnection;
import org.revpay.dao.NotificationDAO;
import org.revpay.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl implements NotificationDAO {

    @Override
    public void save(Notification notification) {

        String sql = "INSERT INTO notifications " +
                "(user_id, title, message, type) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, notification.getUserId());
            ps.setString(2, notification.getTitle());
            ps.setString(3, notification.getMessage());
            ps.setString(4, notification.getType());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving notification", e);
        }
    }

    @Override
    public List<Notification> findByUserId(Long userId) {

        String sql = "SELECT * FROM notifications " +
                "WHERE user_id=? ORDER BY created_at DESC";

        List<Notification> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Notification n = new Notification();
                n.setId(rs.getLong("id"));
                n.setUserId(rs.getLong("user_id"));
                n.setTitle(rs.getString("title"));
                n.setMessage(rs.getString("message"));
                n.setType(rs.getString("type"));
                n.setRead(rs.getBoolean("is_read"));

                list.add(n);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching notifications", e);
        }

        return list;
    }

    @Override
    public void markAsRead(Long notificationId) {

        String sql = "UPDATE notifications SET is_read=TRUE WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, notificationId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating notification", e);
        }
    }
}