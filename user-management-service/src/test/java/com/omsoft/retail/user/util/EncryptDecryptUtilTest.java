package com.omsoft.retail.user.util;

import com.omsoft.retail.user.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class EncryptDecryptUtilTest {

    private static final String SECRET_KEY = "1234567890123456"; // 16 chars for AES
    private static final String ALGORITHM = "AES";

    private EncryptDecryptUtil util;

    @BeforeEach
    void setUp() {
        util = new EncryptDecryptUtil();
        ReflectionTestUtils.setField(util, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(util, "algorithm", ALGORITHM);
    }

    @Test
    void encrypt_decrypt_roundTrip() {
        String plain = "mySecretPassword";
        String encrypted = util.encrypt(plain);
        assertNotNull(encrypted);
        assertNotEquals(plain, encrypted);
        assertEquals(plain, util.decrypt(encrypted));
    }

    @Test
    void encrypt_returnsBase64EncodedString() {
        String encrypted = util.encrypt("test");
        assertNotNull(encrypted);
        assertDoesNotThrow(() -> java.util.Base64.getDecoder().decode(encrypted));
    }

    @Test
    void decrypt_throwsBusinessExceptionOnInvalidInput() {
        assertThrows(BusinessException.class, () -> util.decrypt("not-valid-base64!!!"));
    }
}
