package com.omsoft.retail.user.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidTokenExceptionTest {

    @Test
    void constructor_setsMessage() {
        InvalidTokenException ex = new InvalidTokenException("bad");
        assertTrue(ex.getMessage().contains("Invalid token found"));
        assertTrue(ex.getMessage().contains("bad"));
    }
}
