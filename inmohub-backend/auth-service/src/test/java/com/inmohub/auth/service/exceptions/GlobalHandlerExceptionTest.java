package com.inmohub.auth.service.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests para {@link GlobalHandlerException}.
 * Verifica que cada tipo de excepcion se mapee al codigo HTTP correcto:
 * ResourceNotFoundException -> 404, UserAlreadyExistsException -> 409,
 * RefreshTokenException -> 403, MethodArgumentNotValid -> 400,
 * y excepciones genericas -> 500.
 */
@DisplayName("GlobalHandlerException (Auth)")
class GlobalHandlerExceptionTest {

    private final GlobalHandlerException handler = new GlobalHandlerException();

    @Test
    @DisplayName("Debe manejar ResourceNotFoundException con 404")
    void manejarResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Usuario no encontrado.");

        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Usuario no encontrado.", response.getBody().get("message"));
        assertEquals(404, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Debe manejar UserAlreadyExistsException con 409")
    void manejarUserAlreadyExists() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("El email ya esta en uso");

        ResponseEntity<Map<String, Object>> response = handler.handleUserAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("El email ya esta en uso", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Debe manejar Exception con 500")
    void manejarExcepcionGlobal() {
        Exception ex = new Exception("Error inesperado");

        ResponseEntity<Map<String, Object>> response = handler.handleGlobalException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Debe manejar RefreshTokenException con 403")
    void manejarRefreshTokenException() {
        RefreshTokenException ex = new RefreshTokenException("El Refresh Token ha expirado.");

        ResponseEntity<Map<String, Object>> response = handler.handleTokenRefreshException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("El Refresh Token ha expirado.", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Debe manejar errores de validacion MethodArgumentNotValid")
    void manejarErroresValidacion() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("userCreateDto", "email", "Formato de email invalido");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Formato de email invalido", response.getBody().get("email"));
    }
}
