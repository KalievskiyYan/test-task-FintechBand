package com.test.exception;

/**
 * Represents no such payment request error case
 */
public class NoSuchPaymentRequestException extends RuntimeException {
    public NoSuchPaymentRequestException(String message) {
        super(message);
    }
}
