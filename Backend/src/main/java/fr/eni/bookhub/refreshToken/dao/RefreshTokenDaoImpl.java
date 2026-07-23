package fr.eni.bookhub.refreshToken.dao;

import fr.eni.bookhub.refreshToken.entity.RefreshToken;
import fr.eni.bookhub.refreshToken.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class RefreshTokenDaoImpl implements IRefreshTokenDao {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenRepository.findByTokenHash(tokenHash);
    }
}
