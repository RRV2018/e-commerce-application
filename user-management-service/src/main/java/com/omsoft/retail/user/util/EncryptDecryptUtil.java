package com.omsoft.retail.user.util;

import com.omsoft.retail.user.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptDecryptUtil {

    @Value("${encryption.secret-key}")
    private String secretKey; // 16 chars

    @Value("${encryption.algorithm}")
    private String algorithm; // 16 chars

    public String encrypt(String str) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes()));
        } catch (Exception e) {
            throw new BusinessException("ERRED-001",e.getMessage());
        }
    }

    public String decrypt(String encrypted) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));
        } catch (Exception e) {
            throw new BusinessException("ERRED-002",e.getMessage());
        }
    }
}
