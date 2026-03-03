package com.omsoft.retail.user.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlreadyExistsExceptionTest {

    @Test
    void constructor_setsMessage() {
        AlreadyExistsException ex = new AlreadyExistsException("Email :", "test@example.com");
        assertEquals("The record is already exists Email : : test@example.com", ex.getMessage());
    }

    @Test
    void isRuntimeException() {
        assertThrows(RuntimeException.class, () -> {
            throw new AlreadyExistsException("key", "value");
        });
    }
}
