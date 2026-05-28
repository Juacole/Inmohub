package com.inmohub.property.service.messaging.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para deserializar caracteristicas de propiedades recibidas via mensajeria Kafka.
 * Representa un par clave-valor dentro de un nodo de propiedad en eventos bulk.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FeatureNodeDto(
        String featureName,
        String featureValue
) {}