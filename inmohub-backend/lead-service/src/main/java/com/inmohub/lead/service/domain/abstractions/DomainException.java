package com.inmohub.lead.service.domain.abstractions;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
