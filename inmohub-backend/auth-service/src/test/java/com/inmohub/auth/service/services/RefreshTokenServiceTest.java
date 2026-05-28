package com.inmohub.auth.service.services;

import com.inmohub.auth.service.exceptions.RefreshTokenException;
import com.inmohub.auth.service.models.RefreshToken;
import com.inmohub.auth.service.models.User;
import com.inmohub.auth.service.repositories.IRefreshTokenRepository;
import com.inmohub.auth.service.repositories.IUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para {@link RefreshTokenService}.
 * Verifica la creacion de refresh tokens, la validacion de expiracion y revocacion,
 * y la busqueda por token. Cubre los casos de error: usuario inexistente,
 * token expirado y token revocado.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService")
class RefreshTokenServiceTest {

    @Mock
    private IRefreshTokenRepository refreshTokenRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("Debe crear un refresh token exitosamente")
    void crearRefreshTokenExitoso() {
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        RefreshToken token = refreshTokenService.createRefreshToken(userId);

        assertNotNull(token);
        assertNotNull(token.getToken());
        assertEquals(user, token.getUser());
        assertFalse(token.isRevoked());
        assertNotNull(token.getExpiresAt());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el usuario no existe")
    void crearRefreshTokenUsuarioNoExiste() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.createRefreshToken(userId);
        });

        assertEquals("Usuario no encontrado.", exception.getMessage());
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("verifyExpiration debe pasar si el token no ha expirado ni esta revocado")
    void verificarTokenValido() {
        RefreshToken token = new RefreshToken();
        token.setExpiresAt(LocalDateTime.now().plusDays(1));
        token.setRevoked(false);

        RefreshToken result = refreshTokenService.verifyExpiration(token);

        assertNotNull(result);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("verifyExpiration debe lanzar excepcion si el token ha expirado")
    void tokenExpiradoLanzaExcepcion() {
        RefreshToken token = new RefreshToken();
        token.setExpiresAt(LocalDateTime.now().minusDays(1));
        token.setRevoked(false);

        RefreshTokenException exception = assertThrows(RefreshTokenException.class, () -> {
            refreshTokenService.verifyExpiration(token);
        });

        assertTrue(exception.getMessage().contains("expirado"));
        verify(refreshTokenRepository, times(1)).delete(token);
    }

    @Test
    @DisplayName("verifyExpiration debe lanzar excepcion si el token ha sido revocado")
    void tokenRevocadoLanzaExcepcion() {
        RefreshToken token = new RefreshToken();
        token.setExpiresAt(LocalDateTime.now().plusDays(1));
        token.setRevoked(true);

        RefreshTokenException exception = assertThrows(RefreshTokenException.class, () -> {
            refreshTokenService.verifyExpiration(token);
        });

        assertTrue(exception.getMessage().contains("revocado"));
    }

    @Test
    @DisplayName("findByToken debe retornar el token si existe")
    void buscarTokenExistente() {
        RefreshToken token = new RefreshToken();
        token.setToken("abc123");

        when(refreshTokenRepository.findByToken("abc123")).thenReturn(Optional.of(token));

        Optional<RefreshToken> result = refreshTokenService.findByToken("abc123");

        assertTrue(result.isPresent());
        assertEquals("abc123", result.get().getToken());
    }

    @Test
    @DisplayName("findByToken debe retornar vacio si no existe")
    void buscarTokenInexistente() {
        when(refreshTokenRepository.findByToken("noexiste")).thenReturn(Optional.empty());

        Optional<RefreshToken> result = refreshTokenService.findByToken("noexiste");

        assertTrue(result.isEmpty());
    }
}
