package fr.eni.bookhub.refreshToken.service;

import fr.eni.bookhub.refreshToken.dao.IRefreshTokenDao;
import fr.eni.bookhub.refreshToken.entity.RefreshToken;
import fr.eni.bookhub.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private static final int TOKEN_BYTE_LENGTH = 64;
    private static final long EXPIRATION_DAYS = 7;

    private final IRefreshTokenDao refreshTokenRepository;

    public String createToken(User user) {
        String rawToken = generateRawToken();

        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash(rawToken))
                .user(user)
                .expiresAt(Instant.now().plus(EXPIRATION_DAYS, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(entity);
        return rawToken;
    }

    public RefreshToken validateToken(String rawToken) {

        RefreshToken entity = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (entity.isRevoked() || entity.isExpired()) {
            throw new IllegalArgumentException("Refresh token is no longer valid");
        }

        return entity;
    }

    public String rotateToken(RefreshToken oldToken) {
        String newRawToken = generateRawToken();
        oldToken.setTokenHash(hash(newRawToken));
        oldToken.setExpiresAt(Instant.now().plus(EXPIRATION_DAYS, ChronoUnit.DAYS));
        refreshTokenRepository.save(oldToken);
        return newRawToken;
    }

    public void revokeToken(String rawToken) {
        refreshTokenRepository.findByTokenHash(hash(rawToken))
                .ifPresent(entity -> {
                    entity.setRevoked(true);
                    refreshTokenRepository.save(entity);
                });
    }

    private String generateRawToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
