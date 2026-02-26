package org.revpay.dao.impl;

import org.revpay.config.DBConnection;
import org.revpay.dao.UserDAO;
import org.revpay.model.AccountStatus;
import org.revpay.model.AccountType;
import org.revpay.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserDAOImpl is a CLASS (not interface, not annotation, not exception).
 *
 * It implements all methods defined in UserDAO.
 */
public class UserDAOImpl implements UserDAO {

    // -------------------- SAVE --------------------

    @Override
    public Long save(User user) {

        String sql = "INSERT INTO users " +
                "(full_name, email, phone, password_hash, transaction_pin_hash, " +
                "account_type, account_status, balance, failed_attempts, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, NOW(), NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getTransactionPinHash());
            ps.setString(6, user.getAccountType().name());
            ps.setString(7, user.getAccountStatus().name());
            ps.setBigDecimal(8, user.getBalance());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();  // ADD THIS
            throw new RuntimeException("Error checking existence", e);
        }

        return null;
    }

    // -------------------- FIND METHODS --------------------

    @Override
    public Optional<User> findById(Long id) {
        return findSingle("SELECT * FROM users WHERE id = ?", id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findSingle("SELECT * FROM users WHERE email = ?", email);
    }

    @Override
    public Optional<User> findByPhone(String phoneNumber) {
        return findSingle("SELECT * FROM users WHERE phone = ?", phoneNumber);
    }

    @Override
    public Optional<User> findByEmailOrPhone(String emailOrPhone) {

        String sql = "SELECT * FROM users WHERE email = ? OR phone = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emailOrPhone);
            ps.setString(2, emailOrPhone);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user", e);
        }

        return Optional.empty();
    }

    // -------------------- UPDATE METHODS --------------------

    @Override
    public void update(User user) {

        String sql = "UPDATE users SET full_name=?, email=?, phone=?, " +
                "password_hash=?, transaction_pin_hash=?, account_type=?, " +
                "account_status=?, balance=?, updated_at=NOW() WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getTransactionPinHash());
            ps.setString(6, user.getAccountType().name());
            ps.setString(7, user.getAccountStatus().name());
            ps.setBigDecimal(8, user.getBalance());
            ps.setLong(9, user.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    @Override
    public void updateAccountStatus(Long userId, AccountStatus status) {

        String sql = "UPDATE users SET account_status=?, updated_at=NOW() WHERE id=?";

        executeUpdate(sql, status.name(), userId);
    }

    @Override
    public void incrementFailedAttempts(Long userId) {

        String sql = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE id=?";

        executeUpdate(sql, userId);
    }

    @Override
    public void resetFailedAttempts(Long userId) {

        String sql = "UPDATE users SET failed_attempts = 0 WHERE id=?";

        executeUpdate(sql, userId);
    }

    @Override
    public void updateLastLogin(Long userId) {

        String sql = "UPDATE users SET last_login = NOW() WHERE id=?";

        executeUpdate(sql, userId);
    }

    // -------------------- EXISTS --------------------

    @Override
    public boolean existsByEmail(String email) {
        return exists("SELECT 1 FROM users WHERE email = ?", email);
    }

    @Override
    public boolean existsByPhone(String phoneNumber) {
        return exists("SELECT 1 FROM users WHERE phone = ?", phoneNumber);
    }

    // -------------------- FIND ALL --------------------

    @Override
    public List<User> findAll() {

        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching users", e);
        }

        return users;
    }

    // -------------------- HELPER METHODS --------------------

    private Optional<User> findSingle(String sql, Object param) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, param);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user", e);
        }

        return Optional.empty();
    }

    private boolean exists(String sql, String value) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, value);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("Error checking existence", e);
        }
    }

    private void executeUpdate(String sql, Object... params) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error executing update", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {

        User user = new User();

        user.setId(rs.getLong("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setTransactionPinHash(rs.getString("transaction_pin_hash"));
        user.setAccountType(AccountType.valueOf(rs.getString("account_type")));
        user.setAccountStatus(AccountStatus.valueOf(rs.getString("account_status")));
        user.setBalance(rs.getBigDecimal("balance"));

        return user;
    }
}