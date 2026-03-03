package com.omsoft.retail.user.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void constructor_setsMessageAndErrorCode() {
        BusinessException ex = new BusinessException("ERR-001", "Something went wrong");
        assertEquals("Something went wrong", ex.getMessage());
        assertEquals("ERR-001", ex.getErrorCode());
    }

    @Test
    void isRuntimeException() {
        assertThrows(RuntimeException.class, () -> {
            throw new BusinessException("CODE", "msg");
        });
    }
}
