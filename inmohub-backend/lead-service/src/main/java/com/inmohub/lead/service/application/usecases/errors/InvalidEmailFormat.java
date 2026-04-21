package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

public record InvalidEmailFormat(String message, Exception excepcion) implements Error {
    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Exception getExcepcion() {
        return this.excepcion;
    }
}
