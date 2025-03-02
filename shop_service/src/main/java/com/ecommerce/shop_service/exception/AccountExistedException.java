package com.ecommerce.shop_service.exception;

public class AccountExistedException extends RuntimeException {

    public AccountExistedException(String message) {
        super(message);
    }

    public AccountExistedException(String message, Throwable cause) {
        super(message, cause);
    }
}
