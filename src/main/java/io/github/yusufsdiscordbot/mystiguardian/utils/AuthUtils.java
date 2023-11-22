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

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Getter;
import lombok.val;

@Getter
public class AuthUtils {
    private final KeyPair keyPair;
    private final Algorithm algorithm;

    public AuthUtils() throws NoSuchAlgorithmException {
        val publicKeyAsString = MystiGuardianUtils.jConfig.get("public-key").asText();
        val privateKeyAsString = MystiGuardianUtils.jConfig.get("private-key").asText();

        if (publicKeyAsString == null || privateKeyAsString == null) {
            MystiGuardianUtils.discordAuthLogger.error("No public or private key found in config");
            throw new RuntimeException("No public or private key found in config");
        }

        this.keyPair = getKeys(publicKeyAsString, privateKeyAsString);

        this.algorithm = Algorithm.ECDSA256((ECPublicKey) keyPair.getPublic(), (ECPrivateKey) keyPair.getPrivate());

        JWTVerifier jwtVerifier = JWT.require(algorithm)
                .withIssuer("mystiguardian")
                .build();

        MystiGuardianUtils.discordAuthLogger.info("Successfully loaded public and private key from config");
    }

    private KeyPair getKeys(String publicKeyAsString, String privateKeyAsString) throws NoSuchAlgorithmException {
        PublicKey publicKey = null;
        PrivateKey privateKey = null;

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyAsString.getBytes());
        EncodedKeySpec privateKeySpec = new X509EncodedKeySpec(privateKeyAsString.getBytes());

        try {
            publicKey = keyFactory.generatePublic(publicKeySpec);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (InvalidKeySpecException e) {
            MystiGuardianUtils.discordAuthLogger.error("Error while generating keys", e);
        }

        return new KeyPair(publicKey, privateKey);
    }

    public String generateJwt(String userId, long expiresAt) throws Exception {

        JWTCreator.Builder tokenBuilder = JWT.create()
                .withClaim("jti", UUID.randomUUID().toString())
                .withIssuer("mystiguardian")
                .withClaim("userId", userId)
                .withClaim("expiresAt", expiresAt)
                .withExpiresAt(Instant.ofEpochSecond(expiresAt));

        return tokenBuilder.sign(Algorithm.ECDSA256((ECPublicKey) keyPair.getPublic(), (ECPrivateKey) keyPair.getPrivate()));
    }

}
