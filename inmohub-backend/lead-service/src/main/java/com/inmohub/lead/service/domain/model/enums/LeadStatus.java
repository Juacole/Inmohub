package com.inmohub.lead.service.domain.model.enums;

/**
 * Estados posibles del ciclo de vida de un lead:
 * NEW (nuevo), CONTACTED (contactado), NEGOTIATION (en negociacion),
 * CLOSED (cerrado), LOST (perdido).
 */
public enum LeadStatus {
    NEW,
    CONTACTED,
    NEGOTIATION,
    CLOSED,
    LOST
}
