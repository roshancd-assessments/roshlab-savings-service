package com.roshlab.savings.exception;

public class MaxAccountsPerCustomerException extends RuntimeException {
    public MaxAccountsPerCustomerException(String message) {
        super(message);
    }
}