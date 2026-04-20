package com.inmohub.property.service.dtos;

import jakarta.validation.constraints.NotBlank;

public record PropertyFeatureDto(
        @NotBlank
        String featureName,
        @NotBlank
        String featureValue
) {
}
