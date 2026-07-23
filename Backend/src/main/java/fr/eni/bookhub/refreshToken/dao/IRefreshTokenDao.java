package fr.eni.bookhub.refreshToken.dao;

import fr.eni.bookhub.refreshToken.entity.RefreshToken;

import java.util.Optional;

public interface IRefreshTokenDao {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
