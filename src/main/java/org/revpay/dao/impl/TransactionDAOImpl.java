package org.revpay.dao.impl;

import org.revpay.config.DBConnection;
import org.revpay.dao.TransactionDAO;
import org.revpay.model.Transaction;
import org.revpay.model.TransactionStatus;
import org.revpay.model.TransactionType;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TransactionDAOImpl is a CLASS (not interface, not annotation, not exception).
 *
 * JDBC implementation of TransactionDAO.
 */
public class TransactionDAOImpl implements TransactionDAO {

    // -------------------- SAVE --------------------

    @Override
    public Long save(Transaction transaction) {

        String sql = "INSERT INTO transactions " +
                "(reference, sender_id, receiver_id, amount, type, status, note, " +
                "created_at, updated_at, related_reference) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, transaction.getTransactionReference());
            ps.setLong(2, transaction.getSenderId());
            ps.setLong(3, transaction.getReceiverId());
            ps.setBigDecimal(4, transaction.getAmount());
            ps.setString(5, transaction.getTransactionType().name());
            ps.setString(6, transaction.getTransactionStatus().name());
            ps.setString(7, transaction.getNote());
            ps.setTimestamp(8, Timestamp.valueOf(transaction.getCreatedAt()));
            ps.setTimestamp(9, Timestamp.valueOf(transaction.getUpdatedAt()));
            ps.setString(10, transaction.getRelatedReference());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving transaction", e);
        }

        return null;
    }

    // -------------------- FIND BY ID --------------------

    @Override
    public Optional<Transaction> findById(Long id) {

        String sql = "SELECT * FROM transactions WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding transaction by id", e);
        }

        return Optional.empty();
    }

    // -------------------- FIND BY REFERENCE --------------------

    @Override
    public Optional<Transaction> findByReference(String reference) {

        String sql = "SELECT * FROM transactions WHERE reference = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, reference);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding transaction by reference", e);
        }

        return Optional.empty();
    }

    // -------------------- UPDATE STATUS --------------------

    @Override
    public void updateStatus(String reference, TransactionStatus status) {

        String sql = "UPDATE transactions SET status = ?, updated_at = ? WHERE reference = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, reference);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating transaction status", e);
        }
    }

    // -------------------- FIND BY USER --------------------

    @Override
    public List<Transaction> findByUserId(Long userId) {

        String sql = "SELECT * FROM transactions " +
                "WHERE sender_id = ? OR receiver_id = ? ORDER BY created_at DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                transactions.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transactions by user", e);
        }

        return transactions;
    }

    @Override
    public List<Transaction> findByUserIdAndType(Long userId, TransactionType type) {
        return List.of();
    }

    @Override
    public List<Transaction> findByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    // -------------------- FILTER BY STATUS --------------------

    @Override
    public List<Transaction> findByUserIdAndStatus(Long userId, TransactionStatus status) {

        String sql = "SELECT * FROM transactions " +
                "WHERE (sender_id = ? OR receiver_id = ?) AND status = ? " +
                "ORDER BY created_at DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setString(3, status.name());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                transactions.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transactions by status", e);
        }

        return transactions;
    }

    @Override
    public List<Transaction> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Long id) {

    }

    // -------------------- FILTER BY AMOUNT RANGE --------------------

    @Override
    public List<Transaction> findByUserIdAndAmountRange(Long userId,
                                                        BigDecimal minAmount,
                                                        BigDecimal maxAmount) {

        String sql = "SELECT * FROM transactions " +
                "WHERE (sender_id = ? OR receiver_id = ?) " +
                "AND amount BETWEEN ? AND ? ORDER BY created_at DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setBigDecimal(3, minAmount);
            ps.setBigDecimal(4, maxAmount);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                transactions.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transactions by amount range", e);
        }

        return transactions;
    }

    // -------------------- MAP ROW --------------------

    private Transaction mapRow(ResultSet rs) throws SQLException {

        Transaction transaction = new Transaction();

        transaction.setId(rs.getLong("id"));
        transaction.setTransactionReference(rs.getString("reference"));
        transaction.setSenderId(rs.getLong("sender_id"));
        transaction.setReceiverId(rs.getLong("receiver_id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setTransactionType(
                TransactionType.valueOf(rs.getString("type"))
        );
        transaction.setTransactionStatus(
                TransactionStatus.valueOf(rs.getString("status"))
        );
        transaction.setNote(rs.getString("note"));
        transaction.setRelatedReference(rs.getString("related_reference"));
        transaction.setCreatedAt(
                rs.getTimestamp("created_at").toLocalDateTime()
        );
        transaction.setUpdatedAt(
                rs.getTimestamp("updated_at").toLocalDateTime()
        );

        return transaction;
    }
}