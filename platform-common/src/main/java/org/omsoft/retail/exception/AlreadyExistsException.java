package org.omsoft.retail.exception;

public class AlreadyExistsException  extends RuntimeException {
    public AlreadyExistsException(String key, String value) {
        super("The record is already exists "+key+" : " + value);
    }
}
