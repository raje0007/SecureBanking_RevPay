package org.revpay.dao;

import org.revpay.model.Transaction;
import org.revpay.model.TransactionStatus;
import org.revpay.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Transaction entity.
 *
 * This is an INTERFACE (not class, not annotation, not exception).
 * It defines persistence operations related to financial transactions.
 */
public interface TransactionDAO {

    /**
     * Save a new transaction.
     *
     * @param transaction transaction object
     * @return generated transaction ID
     */
    Long save(Transaction transaction);

    /**
     * Find transaction by database ID.
     *
     * @param id transaction ID
     * @return Optional<Transaction>
     */
    Optional<Transaction> findById(Long id);

    /**
     * Find transaction using public transaction reference.
     *
     * @param reference transaction reference
     * @return Optional<Transaction>
     */
    Optional<Transaction> findByReference(String reference);

    /**
     * Update transaction status.
     *
     * @param reference transaction reference
     * @param status new status
     */
    void updateStatus(String reference, TransactionStatus status);

    /**
     * Retrieve all transactions for a specific user.
     *
     * @param userId user ID
     * @return list of transactions
     */
    List<Transaction> findByUserId(Long userId);

    /**
     * Retrieve transactions filtered by type.
     *
     * @param userId user ID
     * @param type transaction type
     * @return list of transactions
     */
    List<Transaction> findByUserIdAndType(Long userId, TransactionType type);

    /**
     * Retrieve transactions within date range.
     *
     * @param userId user ID
     * @param start start datetime
     * @param end end datetime
     * @return list of transactions
     */
    List<Transaction> findByUserIdAndDateRange(Long userId,
                                               LocalDateTime start,
                                               LocalDateTime end);

    /**
     * Retrieve transactions filtered by amount range.
     *
     * @param userId user ID
     * @param minAmount minimum amount
     * @param maxAmount maximum amount
     * @return list of transactions
     */
    List<Transaction> findByUserIdAndAmountRange(Long userId,
                                                 BigDecimal minAmount,
                                                 BigDecimal maxAmount);

    /**
     * Retrieve transactions filtered by status.
     *
     * @param userId user ID
     * @param status transaction status
     * @return list of transactions
     */
    List<Transaction> findByUserIdAndStatus(Long userId, TransactionStatus status);

    /**
     * Retrieve all transactions (admin / analytics).
     *
     * @return list of all transactions
     */
    List<Transaction> findAll();

    /**
     * Delete a transaction (only allowed in special admin cases).
     *
     * @param id transaction ID
     */
    void deleteById(Long id);
}