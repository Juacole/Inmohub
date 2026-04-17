package com.inmohub.auth.service.services;

import com.inmohub.auth.service.exceptions.RefreshTokenException;
import com.inmohub.auth.service.models.RefreshToken;
import com.inmohub.auth.service.models.User;
import com.inmohub.auth.service.repositories.IRefreshTokenRepository;
import com.inmohub.auth.service.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final IRefreshTokenRepository refreshTokenRepository;
    private final IUserRepository userRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenDurationMs;

    public RefreshToken createRefreshToken(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plusNanos(refreshTokenDurationMs * 1_000_000));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException("El Refresh Token ha expirado. Por favor, inicie sesión de nuevo.");
        }
        if (token.isRevoked()) {
            throw new RefreshTokenException("El Refresh Token ha sido revocado.");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}