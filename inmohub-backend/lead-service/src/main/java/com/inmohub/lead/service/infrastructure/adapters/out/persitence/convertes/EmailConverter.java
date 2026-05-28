package com.inmohub.lead.service.infrastructure.adapters.out.persitence.convertes;

import com.inmohub.lead.service.domain.valueobjetcs.Email;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convertidor JPA que transforma el Value Object {@link Email} a String para la base de datos y viceversa.
 * Se aplica automaticamente a todas las entidades que contengan un Email.
 */
@Converter(autoApply = true) // autoApply hace que se use siempre que encuentre un valu object Email
public class EmailConverter implements AttributeConverter<Email, String> {

    @Override
    public String convertToDatabaseColumn(Email email) {
        return (email == null) ? null : email.value();
    }

    @Override
    public Email convertToEntityAttribute(String dbData) {
        return (dbData == null || dbData.isBlank()) ? null : new Email(dbData);
    }
}