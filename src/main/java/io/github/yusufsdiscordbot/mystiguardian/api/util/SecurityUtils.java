package io.github.yusufsdiscordbot.mystiguardian.api.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.nio.charset.StandardCharsets;
import java.security.Key;

public class SecurityUtils {
    private static final String ALGORITHM = "AES";


    private static Key generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(128); // You can adjust the key size based on your security requirements
        return keyGenerator.generateKey();
    }

    // Encrypt the user ID
    public static String encryptUserId(String userId) throws Exception {
        Key secretKey = generateSecretKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(userId.getBytes(StandardCharsets.UTF_8));
        return new String(encryptedBytes, StandardCharsets.ISO_8859_1);
    }
}
