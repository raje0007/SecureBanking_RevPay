package org.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain Model representing a financial transaction.
 * This is a CLASS (not interface, not annotation, not exception).
 */
public class Transaction {

    private Long id;

    // Unique public reference ID (for users)
    private String transactionReference;

    // Sender and Receiver
    private Long senderId;
    private Long receiverId;

    // Amount (BigDecimal for financial precision)
    private BigDecimal amount;

    // Transaction classification
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;

    // Optional note
    private String note;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For reversal or linking related transactions
    private String relatedReference;

    // =========================
    // Constructors
    // =========================

    public Transaction() {
    }

    public Transaction(Long senderId,
                       Long receiverId,
                       BigDecimal amount,
                       TransactionType transactionType,
                       String note) {

        this.transactionReference = generateReference();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionStatus = TransactionStatus.PENDING;
        this.note = note;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // =========================
    // Private Helper
    // =========================

    private String generateReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    // =========================
    // Business Methods
    // =========================

    public void markCompleted() {
        this.transactionStatus = TransactionStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.transactionStatus = TransactionStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markCancelled() {
        this.transactionStatus = TransactionStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isSuccessful() {
        return this.transactionStatus == TransactionStatus.COMPLETED;
    }

    public boolean isPending() {
        return this.transactionStatus == TransactionStatus.PENDING;
    }

    public boolean isDebitTransaction(Long userId) {
        return senderId != null && senderId.equals(userId);
    }

    public boolean isCreditTransaction(Long userId) {
        return receiverId != null && receiverId.equals(userId);
    }

    // =========================
    // Getters & Setters
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getRelatedReference() {
        return relatedReference;
    }

    public void setRelatedReference(String relatedReference) {
        this.relatedReference = relatedReference;
    }
    //Setter Method


    // -------------------- Transaction Reference --------------------
    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    // -------------------- Sender ID --------------------
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    // -------------------- Receiver ID --------------------
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    // -------------------- Amount --------------------
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // -------------------- Transaction Type --------------------
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    // -------------------- Transaction Status --------------------
    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    // -------------------- Note --------------------
    public void setNote(String note) {
        this.note = note;
    }



    // -------------------- Created At --------------------
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // -------------------- Updated At --------------------
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    // =========================
    // equals & hashCode
    // =========================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionReference, that.transactionReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionReference);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "reference='" + transactionReference + '\'' +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", amount=" + amount +
                ", type=" + transactionType +
                ", status=" + transactionStatus +
                ", createdAt=" + createdAt +
                '}';
    }
}