package com.inmohub.property.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para indicar que un recurso solicitado no existe en el sistema.
 *
 * Uso:Se debe lanzar desde la capa de servicio cuando métodos como {@code findById}
 * no retornan resultados.
 *
 * Comportamiento HTTP: Gracias a la anotación {@link ResponseStatus}, si esta excepción
 * no es capturada manualmente, Spring Boot devolverá automáticamente un código 404 NOT FOUND.
 *
 * Hereda de {@link RuntimeException} para ser una excepción no chequeada,
 * permitiendo un código más limpio sin bloques try-catch obligatorios.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Opcional
public class ResourceNotFoundException extends RuntimeException{

    /**
     * Constructor con mensaje descriptivo.
     *
     * @param message Detalles sobre qué recurso específico no se encontró.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
