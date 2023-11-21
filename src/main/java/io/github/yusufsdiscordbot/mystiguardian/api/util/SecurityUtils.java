/*
 * Copyright 2023 RealYusufIsmail.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package io.github.yusufsdiscordbot.mystiguardian.api.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

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

    // Decrypt the user ID
    public static String decipherUserId(String encryptedUserId) throws Exception {
        Key secretKey = generateSecretKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedUserId.getBytes(StandardCharsets.ISO_8859_1));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
