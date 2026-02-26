package org.revpay.dao.impl;

import org.revpay.config.DBConnection;
import org.revpay.dao.InvoiceDAO;
import org.revpay.model.Invoice;
import org.revpay.model.InvoiceStatus;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of InvoiceDAO.
 */
public class InvoiceDAOImpl implements InvoiceDAO {

    @Override
    public Long save(Invoice invoice) {

        String sql = "INSERT INTO invoices " +
                "(invoice_number, business_user_id, customer_name, customer_email, " +
                "customer_phone, total_amount, paid_amount, status, issue_date, due_date, " +
                "linked_transaction_reference, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, invoice.getInvoiceNumber());
            ps.setLong(2, invoice.getBusinessUserId());
            ps.setString(3, invoice.getCustomerName());
            ps.setString(4, invoice.getCustomerEmail());
            ps.setString(5, invoice.getCustomerPhone());
            ps.setBigDecimal(6, invoice.getTotalAmount());
            ps.setBigDecimal(7, invoice.getPaidAmount());
            ps.setString(8, invoice.getStatus().name());
            ps.setDate(9, Date.valueOf(invoice.getIssueDate()));
            ps.setDate(10, Date.valueOf(invoice.getDueDate()));
            ps.setString(11, invoice.getLinkedTransactionReference());
            ps.setTimestamp(12, Timestamp.valueOf(invoice.getCreatedAt()));
            ps.setTimestamp(13, Timestamp.valueOf(invoice.getUpdatedAt()));

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving invoice", e);
        }

        return null;
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber) {

        String sql = "SELECT * FROM invoices WHERE invoice_number = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, invoiceNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoice by number", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Invoice> findByBusinessUserId(Long businessUserId) {

        String sql = "SELECT * FROM invoices WHERE business_user_id = ? ORDER BY created_at DESC";
        List<Invoice> invoices = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, businessUserId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                invoices.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching invoices by business user", e);
        }

        return invoices;
    }

    @Override
    public List<Invoice> findByBusinessUserIdAndStatus(Long businessUserId, InvoiceStatus status) {
        return List.of();
    }

    @Override
    public List<Invoice> findByBusinessUserIdAndDateRange(Long businessUserId, LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public List<Invoice> findByBusinessUserIdAndAmountRange(Long businessUserId, BigDecimal minAmount, BigDecimal maxAmount) {
        return List.of();
    }

    @Override
    public void updateStatus(String invoiceNumber, InvoiceStatus status) {

        String sql = "UPDATE invoices SET status = ?, updated_at = ? WHERE invoice_number = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, invoiceNumber);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating invoice status", e);
        }
    }

    @Override
    public void updatePaymentDetails(String invoiceNumber,
                                     BigDecimal paidAmount,
                                     String transactionReference) {

        String sql = "UPDATE invoices SET paid_amount = ?, linked_transaction_reference = ?, " +
                "updated_at = ? WHERE invoice_number = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBigDecimal(1, paidAmount);
            ps.setString(2, transactionReference);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, invoiceNumber);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating invoice payment details", e);
        }
    }

    @Override
    public List<Invoice> findOutstandingInvoices(Long businessUserId) {

        String sql = "SELECT * FROM invoices WHERE business_user_id = ? " +
                "AND status IN ('UNPAID','PARTIALLY_PAID') ORDER BY due_date ASC";

        List<Invoice> invoices = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, businessUserId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                invoices.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching outstanding invoices", e);
        }

        return invoices;
    }

    @Override
    public List<Invoice> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Long id) {

    }

    private Invoice mapRow(ResultSet rs) throws SQLException {

        Invoice invoice = new Invoice();

        invoice.setId(rs.getLong("id"));
        invoice.setBusinessUserId(rs.getLong("business_user_id"));
        invoice.setCustomerName(rs.getString("customer_name"));
        invoice.setCustomerEmail(rs.getString("customer_email"));
        invoice.setCustomerPhone(rs.getString("customer_phone"));
        invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
        invoice.setPaidAmount(rs.getBigDecimal("paid_amount"));
        invoice.setStatus(InvoiceStatus.valueOf(rs.getString("status")));
        invoice.setIssueDate(rs.getDate("issue_date").toLocalDate());
        invoice.setDueDate(rs.getDate("due_date").toLocalDate());
        invoice.setLinkedTransactionReference(rs.getString("linked_transaction_reference"));
        invoice.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        invoice.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return invoice;
    }
}