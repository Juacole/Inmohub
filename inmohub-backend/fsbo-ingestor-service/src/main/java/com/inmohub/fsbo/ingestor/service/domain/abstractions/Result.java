package com.inmohub.fsbo.ingestor.service.domain.abstractions;

import java.util.Objects;
import java.util.Optional;

/**
 * Patron Result monadico para representar operaciones que pueden tener exito o fallar.
 * Encapsula un valor de exito o un error, sin lanzar excepciones.
 */
public final class Result<TValue, TError> {
    private final TValue value;
    private final TError error;
    private final boolean isSuccess;

    private Result(TValue value, TError error, boolean isSuccess) {
        this.value = value;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public static <TValue, TError> Result<TValue, TError> success(TValue value) {
        Objects.requireNonNull(value, "El valor de éxito no puede ser nulo.");
        return new Result<>(value, null, true);
    }

    public static <TValue, TError> Result<TValue, TError> error(TError error) {
        Objects.requireNonNull(error, "El error no puede ser nulo");
        return new Result<>(null, error, false);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public TValue getValue() {
        if (!isSuccess) {
            throw new IllegalStateException("No se puede obtener el valor de un resultado fallido.");
        }
        return value;
    }

    public Optional<TError> getErrorValue() {
        return Optional.ofNullable(error);
    }
}