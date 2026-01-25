package com.omsoft.retail.gateway.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String errorMessage) {
        super("Invalid token found please check provided token : " + errorMessage);
    }
}
