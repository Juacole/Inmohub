package com.inmohub.property.service.exceptions;

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
 * Tests para {@link GlobalExceptionHandler}.
 * Verifica que cada tipo de excepcion se mapee al codigo HTTP correcto:
 * ResourceNotFoundException -> 404, UserNotActiveException -> 409,
 * MethodArgumentNotValid -> 400 y excepciones genericas -> 500.
 */
@DisplayName("GlobalExceptionHandler (Property)")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Debe manejar ResourceNotFoundException con 404")
    void manejarResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Propiedad no encontrada.");

        ResponseEntity<Map<String, Object>> response = handler.handlerResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Propiedad no encontrada.", response.getBody().get("message"));
        assertEquals(404, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Debe manejar UserNotActiveException con 409")
    void manejarUserNotActive() {
        ResourceNotFoundException ex = new ResourceNotFoundException("El usuario debe estar activo.");

        ResponseEntity<Map<String, Object>> response = handler.handlerUserNotActive(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().get("message").toString().contains("activo"));
    }

    @Test
    @DisplayName("Debe manejar Exception con 500")
    void manejarExcepcionGlobal() {
        Exception ex = new Exception("Error inesperado");

        ResponseEntity<Map<String, Object>> response = handler.handleGlobalException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del sistema.", response.getBody().get("message"));
        assertEquals(500, response.getBody().get("status"));
    }

    @Test
    @DisplayName("Debe manejar errores de validacion MethodArgumentNotValid")
    void manejarErroresValidacion() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("propertyCreateDto", "title", "El titulo es obligatorio");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El titulo es obligatorio", response.getBody().get("title"));
    }
}
