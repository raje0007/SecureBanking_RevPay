package org.revpay.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.math.BigDecimal;


public class User {

    private Long id;


    private BigDecimal balance = BigDecimal.ZERO;

    // Common Fields
    private String fullName;
    private String email;
    private String phoneNumber;
    private String passwordHash;      // bcrypt hash
    private String transactionPinHash; // hashed PIN
    private String securityQuestion;
    private String securityAnswerHash;

    private AccountType accountType;   // PERSONAL / BUSINESS
    private AccountStatus accountStatus; // ACTIVE / LOCKED / SUSPENDED

    private int failedLoginAttempts;
    private boolean twoFactorEnabled;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;


    // Business-specific fields (nullable for personal accounts)
    private String businessName;
    private String businessType;
    private String taxId;
    private String businessAddress;
    private String verificationDocumentPath;

    // Constructors
    public User() {
    }

    // Constructor for Personal Account
    public User(String fullName, String email, String phoneNumber,
                String passwordHash, String transactionPinHash,
                String securityQuestion, String securityAnswerHash,
                AccountType accountType) {

        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.transactionPinHash = transactionPinHash;
        this.securityQuestion = securityQuestion;
        this.securityAnswerHash = securityAnswerHash;
        this.accountType = accountType;
        this.accountStatus = AccountStatus.ACTIVE;
        this.failedLoginAttempts = 0;
        this.twoFactorEnabled = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // =========================
    // Getters and Setters
    // =========================



    public BigDecimal getBalance() {
        return balance;
    }



    public Long getId() {
        return id;
    }



    public String getFullName() {
        return fullName;
    }


    public String getEmail() {
        return email;
    }

    // -------------------- ID --------------------
    public void setId(Long id) {
        this.id = id;
    }

    // -------------------- Full Name --------------------
    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.updatedAt = LocalDateTime.now();
    }

    // -------------------- Email --------------------
    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    // -------------------- Phone --------------------
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }

    // -------------------- Password --------------------
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = LocalDateTime.now();
    }

    // -------------------- Transaction PIN --------------------
    public void setTransactionPinHash(String transactionPinHash) {
        this.transactionPinHash = transactionPinHash;
        this.updatedAt = LocalDateTime.now();
    }

    // -------------------- Security Question --------------------
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
        this.updatedAt = LocalDateTime.now();
    }

    // -------------------- Security Answer --------------------
    public void setSecurityAnswerHash(String securityAnswerHash) {
        this.securityAnswerHash = securityAnswerHash;
        this.updatedAt = LocalDateTime.now();
    }

    // -------------------- Account Type --------------------
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    // -------------------- Account Status --------------------
    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    // -------------------- Failed Login Attempts --------------------
    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    // -------------------- Two Factor --------------------
    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    // -------------------- Balance (IMPORTANT: BigDecimal) --------------------
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    // -------------------- Created At --------------------
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // -------------------- Updated At --------------------
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // -------------------- Last Login --------------------
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    // -------------------- Business Name --------------------
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    // -------------------- Business Type --------------------
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    // -------------------- Tax ID --------------------
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    // -------------------- Business Address --------------------
    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    // -------------------- Verification Document --------------------
    public void setVerificationDocumentPath(String verificationDocumentPath) {
        this.verificationDocumentPath = verificationDocumentPath;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getTransactionPinHash() {
        return transactionPinHash;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getSecurityAnswerHash() {
        return securityAnswerHash;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }



    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountStatus = AccountStatus.LOCKED;
        }
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }



    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void updateLastLoginTime() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * Update password hash securely.
     */
    public void updatePasswordHash(String newHashedPassword) {
        this.passwordHash = newHashedPassword;
        this.updatedAt = java.time.LocalDateTime.now();
    }

    // =========================
    // Business Fields
    // =========================

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessDetails(String businessName,
                                   String businessType,
                                   String taxId,
                                   String businessAddress,
                                   String verificationDocumentPath) {

        if (this.accountType != AccountType.BUSINESS) {
            throw new IllegalStateException("Only business accounts can set business details.");
        }

        this.businessName = businessName;
        this.businessType = businessType;
        this.taxId = taxId;
        this.businessAddress = businessAddress;
        this.verificationDocumentPath = verificationDocumentPath;
        this.updatedAt = LocalDateTime.now();
    }

    // =========================
    // Utility Methods
    // =========================

    public boolean isAccountLocked() {
        return this.accountStatus == AccountStatus.LOCKED;
    }

    public boolean isBusinessAccount() {
        return this.accountType == AccountType.BUSINESS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", accountType=" + accountType +
                ", accountStatus=" + accountStatus +
                '}';
    }


}