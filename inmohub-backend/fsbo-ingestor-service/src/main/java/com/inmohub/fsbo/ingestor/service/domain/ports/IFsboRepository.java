package com.inmohub.fsbo.ingestor.service.domain.ports;

import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;

/**
 * Puerto de persistencia para guardar lotes FSBO y verificar existencia de propiedades.
 */
public interface IFsboRepository {
    void saveBatch(FsboBatch batch);
    boolean existsByAddressAndCity(String address, String city);
}
