package com.omsoft.retail.user.util;

import java.util.random.RandomGenerator;

public class RandomStringGenerator {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-/*@#$%^()!";
    private RandomStringGenerator() {

    }
    public static String generateString(Integer numberOfChars) {
        return RandomGenerator.getDefault()
                .ints(numberOfChars, 0, CHARS.length())
                .mapToObj(CHARS::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
