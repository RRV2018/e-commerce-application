package com.omsoft.retail.user.exception;

public class AlreadyExistsException  extends RuntimeException {
    public AlreadyExistsException(String key, String value) {
        super("The record is already exists "+key+" : " + value);
    }
}
