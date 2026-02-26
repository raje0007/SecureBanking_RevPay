package org.revpay.dao.impl;

import org.revpay.config.DBConnection;
import org.revpay.dao.LoanDAO;
import org.revpay.model.Loan;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

public class LoanDAOImpl implements LoanDAO {

    @Override
    public Long save(Loan loan) {

        String sql = "INSERT INTO loans " +
                "(user_id, principal_amount, interest_rate, total_amount, " +
                "paid_amount, remaining_amount, start_date, due_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, loan.getUserId());
            ps.setBigDecimal(2, loan.getPrincipalAmount());
            ps.setBigDecimal(3, loan.getInterestRate());
            ps.setBigDecimal(4, loan.getTotalAmount());
            ps.setBigDecimal(5, loan.getPaidAmount());
            ps.setBigDecimal(6, loan.getRemainingAmount());
            ps.setDate(7, Date.valueOf(loan.getStartDate()));
            ps.setDate(8, Date.valueOf(loan.getDueDate()));
            ps.setString(9, loan.getStatus());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException("Error saving loan", e);
        }

        return null;
    }

    @Override
    public Optional<Loan> findByIdAndUser(Long loanId, Long userId) {

        String sql = "SELECT * FROM loans WHERE id=? AND user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, loanId);
            ps.setLong(2, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Loan loan = new Loan();
                loan.setId(rs.getLong("id"));
                loan.setUserId(rs.getLong("user_id"));
                loan.setPrincipalAmount(rs.getBigDecimal("principal_amount"));
                loan.setInterestRate(rs.getBigDecimal("interest_rate"));
                loan.setTotalAmount(rs.getBigDecimal("total_amount"));
                loan.setPaidAmount(rs.getBigDecimal("paid_amount"));
                loan.setRemainingAmount(rs.getBigDecimal("remaining_amount"));
                loan.setStartDate(rs.getDate("start_date").toLocalDate());
                loan.setDueDate(rs.getDate("due_date").toLocalDate());
                loan.setStatus(rs.getString("status"));

                return Optional.of(loan);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching loan", e);
        }

        return Optional.empty();
    }

    @Override
    public void updateLoanAmounts(Long loanId,
                                  BigDecimal paidAmount,
                                  BigDecimal remainingAmount,
                                  String status) {

        String sql = "UPDATE loans SET paid_amount=?, remaining_amount=?, status=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, paidAmount);
            ps.setBigDecimal(2, remainingAmount);
            ps.setString(3, status);
            ps.setLong(4, loanId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating loan", e);
        }
    }
}