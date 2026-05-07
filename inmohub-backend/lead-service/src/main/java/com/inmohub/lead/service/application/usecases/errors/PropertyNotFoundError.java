package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

public class PropertyNotFoundError extends Error {

    public PropertyNotFoundError(String message) {
        super(message, null);
    }

    public PropertyNotFoundError(String message, Exception exception) {
        super(message, exception);
    }
}