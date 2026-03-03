package com.omsoft.retail.user.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomStringGeneratorTest {

    @Test
    void generateString_returnsStringOfCorrectLength() {
        int length = 10;
        String result = RandomStringGenerator.generateString(length);
        assertNotNull(result);
        assertEquals(length, result.length());
    }

    @Test
    void generateString_returnsDifferentStringsEachTime() {
        String first = RandomStringGenerator.generateString(20);
        String second = RandomStringGenerator.generateString(20);
        assertNotEquals(first, second);
    }

    @Test
    void generateString_containsOnlyAllowedCharacters() {
        String allowed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-/*@#$%^()!";
        String result = RandomStringGenerator.generateString(100);
        for (char c : result.toCharArray()) {
            assertTrue(allowed.indexOf(c) >= 0, "Unexpected character: " + c);
        }
    }

    @Test
    void generateString_zeroLength_returnsEmptyString() {
        String result = RandomStringGenerator.generateString(0);
        assertNotNull(result);
        assertEquals("", result);
    }
}
