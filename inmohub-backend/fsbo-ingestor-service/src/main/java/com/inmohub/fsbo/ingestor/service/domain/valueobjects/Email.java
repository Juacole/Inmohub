package com.inmohub.fsbo.ingestor.service.domain.valueobjects;

import com.inmohub.fsbo.ingestor.service.domain.abstractions.DomainException;

import java.util.regex.Pattern;

public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new DomainException("El email no puede estar vacío.");
        }

        if (!isValidEmail(value)) {
            throw new DomainException("Formato del email inválido: " + value);
        }
    }

    private static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}