package com.inmohub.lead.service.domain.abstractions;

/**
 * Tipo unitario (void tipado) para representar operaciones que no retornan valor.
 * Similar al concepto de Unit en programacion funcional.
 */
public final class Unit {
    private Unit() {}
    public static final Unit INSTANCE = new Unit();
}
