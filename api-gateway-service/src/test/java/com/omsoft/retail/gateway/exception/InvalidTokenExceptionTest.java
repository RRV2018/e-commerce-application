package com.omsoft.retail.gateway.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidTokenExceptionTest {

    @Test
    void constructor_setsMessage() {
        InvalidTokenException ex = new InvalidTokenException("bad token");
        assertTrue(ex.getMessage().contains("Invalid token found"));
        assertTrue(ex.getMessage().contains("bad token"));
    }

    @Test
    void isRuntimeException() {
        assertThrows(RuntimeException.class, () -> {
            throw new InvalidTokenException("err");
        });
    }
}
