package com.omsoft.retail.user.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptDecryptUtil {
    private static final String SECRET_KEY = "MySuperSecretKey"; // 16 chars

    public static String encrypt(String str) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encrypted) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
