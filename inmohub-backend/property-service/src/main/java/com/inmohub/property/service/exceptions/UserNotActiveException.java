package com.inmohub.property.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de dominio que representa la violación de una regla de negocio específica:
 * "Solo los usuarios con estado ACTIVO pueden realizar operaciones de escritura."
 *
 * Uso: Se lanza durante el proceso de creación de una propiedad, tras validar
 * el estado del propietario contra el microservicio de autenticación.
 *
 * Comportamiento HTTP: Se mapea al código 409 CONFLICT, ya que la solicitud es válida
 * sintácticamente, pero entra en conflicto con el estado actual del recurso.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserNotActiveException extends RuntimeException {

    /**
     * Constructor por defecto con un mensaje estándar predefinido.
     */
    public UserNotActiveException() {
        super("El propietario no está activo y no puede publicar propiedades.");
    }

    /**
     * Constructor que permite especificar un mensaje personalizado.
     *
     * @param message Descripción detallada del motivo del rechazo.
     */
    public UserNotActiveException(String message) {
        super(message);
    }
}
