package com.inmohub.auth.service.exceptions;

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
 * Capturador global de excepciones para el microservicio Auth.
 *
 * Utiliza AOP (Programación Orientada a Aspectos) a través de {@link RestControllerAdvice}
 * para interceptar errores lanzados en cualquier controlador y transformarlos
 * en respuestas JSON estandarizadas y amigables para el cliente.
 */
@RestControllerAdvice
@Slf4j
public class GlobalHandlerException extends RuntimeException {

    /**
     * Construye una respuesta de error estructurada.
     *
     * @param message Mensaje descriptivo del error.
     * @param status  Código de estado HTTP.
     * @return ResponseEntity con el cuerpo del error (timestamp, mensaje, código).
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
     * Maneja excepciones de recurso no encontrado "404".
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException e) {
        log.warn("Recurso no encontrado: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de conflicto "409".
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(UserAlreadyExistsException e) {
        log.warn("Conflicto de registro: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Maneja cualquier error no controlado "500".
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception e) {
        log.error("Error interno no controlado en Auth-Service", e);
        return buildResponse("Ocurrió un error interno. Por favor contacte al soporte.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja errores de validación de argumentos "@Valid".
     * Captura los errores de los DTOs y devuelve un mapa campo-error.
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

    /**
     * Maneja errores en referencia al refresh token.
     */
    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<Map<String, Object>> handleTokenRefreshException(RefreshTokenException e) {
        log.warn("Intento de uso de token inválido: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.FORBIDDEN);
    }
}
