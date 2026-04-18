package com.inmohub.property.service.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestor centralizado de excepciones para el microservicio de Propiedades.
 * Intercepta los errores lanzados en cualquier capa y devuelve respuestas JSON normalizadas.
 */
@RestControllerAdvice // Con esta anotación, Spring gestiona automaticamente todas las excepciones del controlador
@Slf4j
public class GlobalExceptionHandler extends RuntimeException {

    /**
     * Método auxiliar para construir la respuesta de error con una estructura consistente.
     *
     * @param message Mensaje descriptivo del error para el cliente.
     * @param status  Código de estado HTTP asociado.
     * @return {@link ResponseEntity} conteniendo el cuerpo de la respuesta (timestamp, mensaje, status, error).
     */
    private ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());

        return new ResponseEntity<>(response, status);
    }

    /**
     * Intercepta excepciones de tipo {@link ResourceNotFoundException}.
     *
     * Se activa cuando se intenta acceder a una propiedad o recurso que no existe en la base de datos.
     *
     * @param e La excepción capturada con el mensaje del recurso faltante.
     * @return Respuesta HTTP 404 Not Found con estructura JSON estándar.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerResourceNotFound(ResourceNotFoundException e) {
        log.error("Elemento no encontrado: {}", e.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("message", e.getMessage());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());

        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Intercepta excepciones de regla de negocio {@link UserNotActiveException}.
     *
     * Se activa cuando se valida que el propietario de una propiedad no tiene el estado "ACTIVE".
     * Esto previene la creación de inmuebles por usuarios bloqueados o inactivos.
     *
     * @param e La excepción capturada indicando la violación de la regla de negocio.
     * @return Respuesta HTTP 409 Conflict, indicando que la solicitud entra en conflicto con el estado actual del usuario.
     */
    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<Map<String, Object>> handlerUserNotActive(ResourceNotFoundException e) {
        log.warn("Intento de publicación bloqueada: {}", e.getMessage());

        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Manejador genérico para cualquier excepción no controlada explícitamente.
     *
     * Actúa como red de seguridad para capturar {@link NullPointerException}, fallos de base de datos
     * inesperados, o errores de sistema. Oculta el mensaje técnico original por seguridad.
     *
     * @param e La excepción inesperada.
     * @return Respuesta HTTP 500 Internal Server Error con un mensaje genérico para el cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception e) {
        log.error("Error inesperado en el sistema", e);

        return buildResponse("Error interno del sistema.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Intercepta errores de validación de datos de entrada.
     *
     * Se activa automáticamente cuando fallan las validaciones {@code @Valid} en los DTOs de los controladores.
     * Itera sobre todos los campos fallidos y construye un mapa detallado de errores.
     *
     * @param ex La excepción que contiene la lista de violaciones de restricciones.
     * @return Respuesta HTTP 400 Bad Request con un mapa JSON.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
