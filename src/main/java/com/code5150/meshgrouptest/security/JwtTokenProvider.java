package com.code5150.meshgrouptest.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String USER_ID_CLAIM = "userId";

    private final JWSSigner signer;
    private final JWSVerifier verifier;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration-ms}") long expirationMs) throws JOSEException {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.signer = new MACSigner(secretBytes);
        this.verifier = new MACVerifier(secretBytes);
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim(USER_ID_CLAIM, userId)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + expirationMs))
                .build();

        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        try {
            jwt.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to sign JWT", e);
        }
        return jwt.serialize();
    }

    public Long extractUserId(String token) {
        SignedJWT jwt = parseToken(token);
        try {
            return jwt.getJWTClaimsSet().getLongClaim(USER_ID_CLAIM);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to extract userId from JWT", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT jwt = parseToken(token);
            if (jwt.getJWTClaimsSet().getExpirationTime().before(new Date())) {
                log.debug("JWT token expired");
                return false;
            }
            return jwt.verify(verifier);
        } catch (ParseException | JOSEException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    private SignedJWT parseToken(String token) {
        try {
            return SignedJWT.parse(token);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}
