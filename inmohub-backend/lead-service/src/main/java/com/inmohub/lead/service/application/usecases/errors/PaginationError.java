package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

public class PaginationError extends Error {

    public PaginationError(String message) {
        super(message, null);
    }

    public PaginationError(String message, Exception exception) {
        super(message, exception);
    }
}