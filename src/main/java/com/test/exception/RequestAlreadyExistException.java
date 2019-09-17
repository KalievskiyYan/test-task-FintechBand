package com.test.exception;

/**
 * Represents request already exist error case
 */
public class RequestAlreadyExistException extends RuntimeException {
    public RequestAlreadyExistException(String message) {
        super(message);
    }
}
