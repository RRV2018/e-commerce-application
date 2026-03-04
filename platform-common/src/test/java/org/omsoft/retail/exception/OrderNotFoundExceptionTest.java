package org.omsoft.retail.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderNotFoundExceptionTest {

    @Test
    void constructor_setsMessage() {
        OrderNotFoundException ex = new OrderNotFoundException(12345L);
        assertEquals("Order not found: 12345", ex.getMessage());
    }

    @Test
    void isRuntimeException() {
        assertThrows(RuntimeException.class, () -> {
            throw new OrderNotFoundException(1L);
        });
    }
}
