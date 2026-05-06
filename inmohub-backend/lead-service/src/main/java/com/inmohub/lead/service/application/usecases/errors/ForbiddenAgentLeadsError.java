package com.inmohub.lead.service.application.usecases.errors;

import com.inmohub.lead.service.domain.abstractions.Error;

public class ForbiddenAgentLeadsError extends Error {

    public ForbiddenAgentLeadsError(String message) {
        super(message, null);
    }

    public ForbiddenAgentLeadsError(String message, Exception exception) {
        super(message, exception);
    }
}