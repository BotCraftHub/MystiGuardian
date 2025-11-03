/*
 * Copyright 2025 RealYusufIsmail.
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
package io.github.yusufsdiscordbot.mystiguardian.oauth.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.yusufsdiscordbot.mystiguardian.oauth.entites.OAuthJWt;
import io.github.yusufsdiscordbot.mystiguardian.oauth.entites.impl.OAuthJWtImpl;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bouncycastle.util.io.pem.PemReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spark.Response;

/** Utility class for JWT (JSON Web Token) operations including generation and validation. */
@Slf4j
public class JWTUtils {
    private final KeyPair keyPair;

    /** The JWT verifier used to validate incoming tokens. */
    public static JWTVerifier verifier;

    private static final String PUBLIC_KEY = System.getProperty("user.home") + "/public_key.pem";
    private static final String PUBLIC_KEY_HOSTING = "./public_key.pem";
    private static final String PRIVATE_KEY = System.getProperty("user.home") + "/private_key.pem";
    private static final String PRIVATE_KEY_HOSTING = "./private_key.pem";
    private static final String JWT_PREFIX = "jwt ";

    /**
     * Constructs a new JWTUtils instance and loads RSA key pair.
     *
     * @throws IOException if key files cannot be read
     */
    public JWTUtils() throws IOException {
        this.keyPair = getKeys();

        val algorithm =
                Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());

        verifier = JWT.require(algorithm).withIssuer("mystiguardian").build();

        logger.info("Successfully loaded public and private key from config");
    }

    private KeyPair getKeys() throws IOException {
        PublicKey publicKey = null;
        PrivateKey privateKey = null;

        try {

            if (new File(PUBLIC_KEY).exists()) {
                publicKey = readPublicKeyFromFile(PUBLIC_KEY, "RSA");
            } else if (new File(PUBLIC_KEY_HOSTING).exists()) {
                publicKey = readPublicKeyFromFile(PUBLIC_KEY_HOSTING, "RSA");
            }
        } catch (IOException e) {
            logger.error("Failed to read public key from config", e);
        }

        try {

            if (new File(PRIVATE_KEY).exists()) {
                privateKey = readPrivateKeyFromFile(PRIVATE_KEY, "RSA");
            } else if (new File(PRIVATE_KEY_HOSTING).exists()) {
                privateKey = readPrivateKeyFromFile(PRIVATE_KEY_HOSTING, "RSA");
            }

        } catch (IOException e) {
            logger.error("Failed to read private key from config", e);
        }

        return new KeyPair(publicKey, privateKey);
    }

    private static PublicKey getPublicKey(byte[] keyBytes, String algorithm) {
        PublicKey publicKey = null;
        try {
            val kf = KeyFactory.getInstance(algorithm);
            val keySpec = new X509EncodedKeySpec(keyBytes);
            publicKey = kf.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            logger.error(
                    "Could not reconstruct the public key, the given algorithm could not be found.", e);
        } catch (InvalidKeySpecException e) {
            logger.error("Could not reconstruct the public key", e);
        }

        return publicKey;
    }

    private static PrivateKey getPrivateKey(byte[] keyBytes, String algorithm) {
        PrivateKey privateKey = null;
        try {
            val kf = KeyFactory.getInstance(algorithm);
            val keySpec = new PKCS8EncodedKeySpec(keyBytes);
            privateKey = kf.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            logger.error(
                    "Could not reconstruct the private key, the given algorithm could not be found.", e);
        } catch (InvalidKeySpecException e) {
            logger.error("Could not reconstruct the private key", e);
        }

        return privateKey;
    }

    /**
     * Reads a public key from a PEM file.
     *
     * @param filepath the path to the PEM file
     * @param algorithm the key algorithm (e.g., "RSA")
     * @return the public key
     * @throws IOException if the file cannot be read
     */
    public static PublicKey readPublicKeyFromFile(String filepath, String algorithm)
            throws IOException {
        val bytes = JWTUtils.parsePEMFile(new File(filepath));
        return JWTUtils.getPublicKey(bytes, algorithm);
    }

    /**
     * Reads a private key from a PEM file.
     *
     * @param filepath the path to the PEM file
     * @param algorithm the key algorithm (e.g., "RSA")
     * @return the private key
     * @throws IOException if the file cannot be read
     */
    public static PrivateKey readPrivateKeyFromFile(String filepath, String algorithm)
            throws IOException {
        val bytes = JWTUtils.parsePEMFile(new File(filepath));
        return JWTUtils.getPrivateKey(bytes, algorithm);
    }

    private static byte[] parsePEMFile(File pemFile) throws IOException {
        if (!pemFile.isFile() || !pemFile.exists()) {
            throw new FileNotFoundException(
                    String.format("The file '%s' doesn't exist.", pemFile.getAbsolutePath()));
        }

        byte[] content;
        try (val reader = new PemReader(new FileReader(pemFile))) {
            val pemObject = reader.readPemObject();
            content = pemObject.getContent();
        }
        return content;
    }

    /**
     * Generates a JWT token for a user.
     *
     * @param userId the Discord user ID
     * @param expiresAt the expiration timestamp (epoch seconds)
     * @param id the database record ID
     * @return the signed JWT token string
     */
    public String generateJwt(long userId, long expiresAt, long id) {

        val tokenBuilder =
                JWT.create()
                        .withClaim("jti", UUID.randomUUID().toString())
                        .withIssuer("mystiguardian")
                        .withClaim("user_id", userId)
                        .withClaim("expiration_time", expiresAt)
                        .withClaim("database_id", id)
                        .withExpiresAt(Instant.ofEpochSecond(expiresAt));

        return tokenBuilder.sign(
                Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate()));
    }

    @Nullable
    private OAuthJWt validateJwt(@NotNull String jwt) {
        try {
            return new OAuthJWtImpl(verifier.verify(jwt));
        } catch (Exception e) {
            logger.error("Failed to validate jwt", e);
            return null;
        }
    }

    /**
     * Validates a JWT token from an HTTP request.
     *
     * @param jwt the JWT token string (may include "jwt " prefix)
     * @param response the HTTP response to set status codes
     * @return Optional containing the validated JWT, or empty if invalid
     */
    public Optional<OAuthJWt> validateJwt(String jwt, Response response) {
        if (jwt == null || !jwt.startsWith(JWT_PREFIX)) {
            response.status(401);
            logger.info("JWT not found");
            return Optional.empty();
        }

        return Optional.ofNullable(validateJwt(jwt.substring(JWT_PREFIX.length())));
    }
}
