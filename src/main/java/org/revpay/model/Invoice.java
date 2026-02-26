package org.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain Model representing a Business Invoice.
 * This is a CLASS (not interface, not annotation, not exception).
 */
public class Invoice {

    private Long id;

    // Public invoice reference
    private String invoiceNumber;

    // Business (issuer)
    private Long businessUserId;

    // Customer information
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Financial details
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;

    private InvoiceStatus status;

    // Payment details
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String linkedTransactionReference;

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // =========================
    // Constructors
    // =========================

    public Invoice() {
    }

    public Invoice(Long businessUserId,
                   String customerName,
                   String customerEmail,
                   String customerPhone,
                   BigDecimal totalAmount,
                   LocalDate dueDate) {

        this.invoiceNumber = generateInvoiceNumber();
        this.businessUserId = businessUserId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.totalAmount = totalAmount;
        this.paidAmount = BigDecimal.ZERO;
        this.status = InvoiceStatus.UNPAID;
        this.issueDate = LocalDate.now();
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // =========================
    // Private Helper
    // =========================

    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }

    // =========================
    // Business Logic Methods
    // =========================

    public void markAsPaid(String transactionReference) {
        this.paidAmount = this.totalAmount;
        this.status = InvoiceStatus.PAID;
        this.linkedTransactionReference = transactionReference;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsPartiallyPaid(BigDecimal amountPaid, String transactionReference) {
        this.paidAmount = this.paidAmount.add(amountPaid);
        this.status = InvoiceStatus.PARTIALLY_PAID;
        this.linkedTransactionReference = transactionReference;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCancelled() {
        this.status = InvoiceStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && status != InvoiceStatus.PAID;
    }

    public BigDecimal getRemainingAmount() {
        return totalAmount.subtract(paidAmount);
    }

    // =========================
    // Getters and Setters
    // =========================

    // -------------------- Business User ID --------------------
    public void setBusinessUserId(Long businessUserId) {
        this.businessUserId = businessUserId;
    }

    // -------------------- Customer Name --------------------
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // -------------------- Customer Email --------------------
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    // -------------------- Customer Phone --------------------
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    // -------------------- Total Amount --------------------
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    // -------------------- Paid Amount --------------------
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    // -------------------- Status --------------------
    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    // -------------------- Issue Date --------------------
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    // -------------------- Due Date --------------------
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // -------------------- Linked Transaction Reference --------------------
    public void setLinkedTransactionReference(String linkedTransactionReference) {
        this.linkedTransactionReference = linkedTransactionReference;
    }

    // -------------------- Created At --------------------
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // -------------------- Updated At --------------------
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public Long getBusinessUserId() {
        return businessUserId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getLinkedTransactionReference() {
        return linkedTransactionReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // =========================
    // equals & hashCode
    // =========================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invoice)) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(invoiceNumber, invoice.invoiceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceNumber);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceNumber='" + invoiceNumber + '\'' +
                ", businessUserId=" + businessUserId +
                ", totalAmount=" + totalAmount +
                ", paidAmount=" + paidAmount +
                ", status=" + status +
                ", dueDate=" + dueDate +
                '}';
    }
}