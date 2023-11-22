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
package io.github.yusufsdiscordbot.mystiguardian.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

@Getter
public class AuthUtils {
    private final KeyPair keyPair;
    private final Algorithm algorithm;
    public static JWTVerifier verifier;
    private static final String PUBLIC_KEY = System.getProperty("user.home") + "/public_key.pem";
    private static final String PRIVATE_KEY = System.getProperty("user.home") + "/private_key.pem";

    public AuthUtils() throws IOException {
        this.keyPair = getKeys();

        this.algorithm = Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());

        verifier = JWT.require(algorithm).withIssuer("mystiguardian").build();

        MystiGuardianUtils.discordAuthLogger.info("Successfully loaded public and private key from config");
    }

    private KeyPair getKeys() throws IOException {
        PublicKey publicKey = null;
        PrivateKey privateKey = null;

        try {
            // Change the algorithm to "RSA" here
            publicKey = readPublicKeyFromFile(AuthUtils.PUBLIC_KEY, "RSA");
        } catch (IOException e) {
            MystiGuardianUtils.discordAuthLogger.error("Failed to read public key from config", e);
        }

        try {
            // Change the algorithm to "RSA" here
            privateKey = readPrivateKeyFromFile(AuthUtils.PRIVATE_KEY, "RSA");
        } catch (IOException e) {
            MystiGuardianUtils.discordAuthLogger.error("Failed to read private key from config", e);
        }

        return new KeyPair(publicKey, privateKey);
    }

    private static PublicKey getPublicKey(byte[] keyBytes, String algorithm) {
        PublicKey publicKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            publicKey = kf.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            MystiGuardianUtils.discordAuthLogger.error(
                    "Could not reconstruct the public key, the given algorithm could not be found.", e);
        } catch (InvalidKeySpecException e) {
            MystiGuardianUtils.discordAuthLogger.error("Could not reconstruct the public key", e);
        }

        return publicKey;
    }

    private static PrivateKey getPrivateKey(byte[] keyBytes, String algorithm) {
        PrivateKey privateKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            privateKey = kf.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            MystiGuardianUtils.discordAuthLogger.error(
                    "Could not reconstruct the private key, the given algorithm could not be found.", e);
        } catch (InvalidKeySpecException e) {
            MystiGuardianUtils.discordAuthLogger.error("Could not reconstruct the private key", e);
        }

        return privateKey;
    }

    public static PublicKey readPublicKeyFromFile(String filepath, String algorithm) throws IOException {
        byte[] bytes = AuthUtils.parsePEMFile(new File(filepath));
        return AuthUtils.getPublicKey(bytes, algorithm);
    }

    public static PrivateKey readPrivateKeyFromFile(String filepath, String algorithm) throws IOException {
        byte[] bytes = AuthUtils.parsePEMFile(new File(filepath));
        return AuthUtils.getPrivateKey(bytes, algorithm);
    }

    private static byte[] parsePEMFile(File pemFile) throws IOException {
        if (!pemFile.isFile() || !pemFile.exists()) {
            throw new FileNotFoundException(String.format("The file '%s' doesn't exist.", pemFile.getAbsolutePath()));
        }

        byte[] content;
        try (PemReader reader = new PemReader(new FileReader(pemFile))) {
            PemObject pemObject = reader.readPemObject();
            content = pemObject.getContent();
        }
        return content;
    }

    public String generateJwt(String userId, long expiresAt) throws Exception {

        JWTCreator.Builder tokenBuilder = JWT.create()
                .withClaim("jti", UUID.randomUUID().toString())
                .withIssuer("mystiguardian")
                .withClaim("userId", userId)
                .withClaim("expiresAt", expiresAt)
                .withExpiresAt(Instant.ofEpochSecond(expiresAt));

        return tokenBuilder.sign(
                Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate()));
    }
}
