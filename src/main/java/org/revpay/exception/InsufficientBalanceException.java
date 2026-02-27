package org.revpay.exception;

/**
 * Custom Exception for insufficient wallet balance.
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}