package org.revpay.dao;

import org.revpay.model.Invoice;
import org.revpay.model.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Invoice entity.
 *
 * This is an INTERFACE (not class, not annotation, not exception).
 * It defines database operations related to business invoices.
 */
public interface InvoiceDAO {

    /**
     * Save a new invoice.
     *
     * @param invoice invoice object
     * @return generated invoice ID
     */
    Long save(Invoice invoice);

    /**
     * Find invoice by database ID.
     *
     * @param id invoice ID
     * @return Optional<Invoice>
     */
    Optional<Invoice> findById(Long id);

    /**
     * Find invoice by invoice number (public reference).
     *
     * @param invoiceNumber invoice reference
     * @return Optional<Invoice>
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    /**
     * Retrieve all invoices created by a business user.
     *
     * @param businessUserId business user ID
     * @return list of invoices
     */
    List<Invoice> findByBusinessUserId(Long businessUserId);

    /**
     * Retrieve invoices filtered by status.
     *
     * @param businessUserId business user ID
     * @param status invoice status
     * @return list of invoices
     */
    List<Invoice> findByBusinessUserIdAndStatus(Long businessUserId,
                                                InvoiceStatus status);

    /**
     * Retrieve invoices within a date range.
     *
     * @param businessUserId business user ID
     * @param startDate start date
     * @param endDate end date
     * @return list of invoices
     */
    List<Invoice> findByBusinessUserIdAndDateRange(Long businessUserId,
                                                   LocalDate startDate,
                                                   LocalDate endDate);

    /**
     * Retrieve invoices within amount range.
     *
     * @param businessUserId business user ID
     * @param minAmount minimum amount
     * @param maxAmount maximum amount
     * @return list of invoices
     */
    List<Invoice> findByBusinessUserIdAndAmountRange(Long businessUserId,
                                                     BigDecimal minAmount,
                                                     BigDecimal maxAmount);

    /**
     * Update invoice status.
     *
     * @param invoiceNumber invoice reference
     * @param status new status
     */
    void updateStatus(String invoiceNumber, InvoiceStatus status);

    /**
     * Update paid amount and link transaction reference.
     *
     * @param invoiceNumber invoice reference
     * @param paidAmount total paid amount
     * @param transactionReference linked transaction reference
     */
    void updatePaymentDetails(String invoiceNumber,
                              BigDecimal paidAmount,
                              String transactionReference);

    /**
     * Retrieve all unpaid invoices (for analytics/dashboard).
     *
     * @param businessUserId business user ID
     * @return list of unpaid invoices
     */
    List<Invoice> findOutstandingInvoices(Long businessUserId);

    /**
     * Retrieve all invoices (admin / analytics use).
     *
     * @return list of all invoices
     */
    List<Invoice> findAll();

    /**
     * Delete invoice (allowed only if unpaid and not processed).
     *
     * @param id invoice ID
     */
    void deleteById(Long id);
}