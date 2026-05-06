package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

public class AccessDeniedError extends Error {

    public AccessDeniedError(String message) {
        super(message, null);
    }

    public AccessDeniedError(String message, Exception exception) {
        super(message, exception);
    }
}