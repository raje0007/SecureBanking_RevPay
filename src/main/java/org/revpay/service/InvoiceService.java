package org.revpay.service;

import org.revpay.dao.InvoiceDAO;
import org.revpay.dao.impl.InvoiceDAOImpl;
import org.revpay.model.Invoice;
import org.revpay.model.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * InvoiceService is a CLASS (not interface, not annotation, not exception).
 *
 * It handles business logic related to:
 * - Invoice creation
 * - Invoice retrieval
 * - Outstanding invoice tracking
 */
public class InvoiceService {

    private final InvoiceDAO invoiceDAO;

    public InvoiceService() {
        this.invoiceDAO = new InvoiceDAOImpl();
    }

    /**
     * Create new invoice
     */
    public Long createInvoice(Long businessUserId,
                              String customerName,
                              String customerEmail,
                              String customerPhone,
                              BigDecimal amount,
                              LocalDate dueDate) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invoice amount must be greater than zero.");
        }

        if (dueDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Due date cannot be in the past.");
        }

        Invoice invoice = new Invoice(
                businessUserId,
                customerName,
                customerEmail,
                customerPhone,
                amount,
                dueDate
        );

        return invoiceDAO.save(invoice);
    }

    /**
     * Get all invoices for business user
     */
    public List<Invoice> getInvoicesByBusinessUser(Long businessUserId) {
        return invoiceDAO.findByBusinessUserId(businessUserId);
    }

    /**
     * Get outstanding invoices
     */
    public List<Invoice> getOutstandingInvoices(Long businessUserId) {
        return invoiceDAO.findOutstandingInvoices(businessUserId);
    }

    /**
     * Mark invoice as paid
     */
    public void markInvoiceAsPaid(String invoiceNumber,
                                  BigDecimal paidAmount,
                                  String transactionReference) {

        Optional<Invoice> optionalInvoice =
                invoiceDAO.findByInvoiceNumber(invoiceNumber);

        if (optionalInvoice.isEmpty()) {
            throw new RuntimeException("Invoice not found.");
        }

        Invoice invoice = optionalInvoice.get();

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Invoice already paid.");
        }

        invoiceDAO.updatePaymentDetails(
                invoiceNumber,
                paidAmount,
                transactionReference
        );

        if (paidAmount.compareTo(invoice.getTotalAmount()) >= 0) {
            invoiceDAO.updateStatus(invoiceNumber, InvoiceStatus.PAID);
        } else {
            invoiceDAO.updateStatus(invoiceNumber, InvoiceStatus.PARTIALLY_PAID);
        }
    }
}