package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.CreateLeadRequest;
import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.model.enums.LeadSource;
import com.inmohub.lead.service.domain.model.enums.LeadStatus;
import com.inmohub.lead.service.domain.ports.ILeadRepository;
import com.inmohub.lead.service.domain.valueobjetcs.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para {@link CreateLeadUseCase}.
 * Verifica la creacion de leads con validacion de campos obligatorios
 * (nombre, email, telefono, propiedad), formato de email y fuente del lead.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateLeadUseCase")
class CreateLeadUseCaseTest {

    @Mock
    private ILeadRepository leadRepository;

    @InjectMocks
    private CreateLeadUseCase createLeadUseCase;

    private final UUID propertyId = UUID.randomUUID();
    private final CreateLeadRequest requestValido = new CreateLeadRequest(
            "Pepe Montana", "pepe.montana@gmail.com", "600123456",
            "Quiero informacion", LeadSource.WEB, propertyId
    );

    @Test
    @DisplayName("Debe crear un lead exitosamente")
    void crearLeadExitoso() {
        Lead leadGuardado = Lead.create("Pepe Montana", new Email("pepe.montana@gmail.com"),
                "600123456", "Quiero informacion", LeadSource.WEB, propertyId);

        when(leadRepository.saveLead(any(Lead.class))).thenReturn(leadGuardado);

        Result<LeadResponse, Error> result = createLeadUseCase.execute(requestValido);

        assertTrue(result.isSuccess());
        LeadResponse response = result.getValue();
        assertEquals("Pepe Montana", response.name());
        assertEquals("pepe.montana@gmail.com", response.email());
        assertEquals("600123456", response.phone());
        assertEquals(LeadStatus.NEW, response.status());
        assertEquals(propertyId, response.propertyId());
        verify(leadRepository, times(1)).saveLead(any(Lead.class));
    }

    @Test
    @DisplayName("Debe fallar si el request es null")
    void requestNuloRetornaError() {
        Result<LeadResponse, Error> result = createLeadUseCase.execute(null);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorValue().isPresent());
        assertEquals("La solicitud no puede ser nula.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el nombre es null")
    void nombreNuloRetornaError() {
        CreateLeadRequest request = new CreateLeadRequest(
                null, "pepe.montana@gmail.com", "600123456", "Info", LeadSource.WEB, propertyId);

        Result<LeadResponse, Error> result = createLeadUseCase.execute(request);

        assertFalse(result.isSuccess());
        assertEquals("El nombre es obligatorio.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el nombre esta vacio")
    void nombreVacioRetornaError() {
        CreateLeadRequest request = new CreateLeadRequest(
                "", "pepe.montana@gmail.com", "600123456", "Info", LeadSource.WEB, propertyId);

        Result<LeadResponse, Error> result = createLeadUseCase.execute(request);

        assertFalse(result.isSuccess());
        assertEquals("El nombre es obligatorio.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el email es null")
    void emailNuloRetornaError() {
        CreateLeadRequest request = new CreateLeadRequest(
                "Pepe", null, "600123456", "Info", LeadSource.WEB, propertyId);

        Result<LeadResponse, Error> result = createLeadUseCase.execute(request);

        assertFalse(result.isSuccess());
        assertEquals("El email es obligatorio.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el email esta vacio")
    void emailVacioRetornaError() {
        CreateLeadRequest request = new CreateLeadRequest(
                "Pepe", "", "600123456", "Info", LeadSource.WEB, propertyId);

        Result<LeadResponse, Error> result = createLeadUseCase.execute(request);

        assertFalse(result.isSuccess());
        assertEquals("El email es obligatorio.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el formato del email es invalido")
    void emailFormatoInvalidoRetornaError() {
        CreateLeadRequest request = new CreateLeadRequest(
                "Pepe", "no-es-email", "600123456", "Info", LeadSource.WEB, propertyId);

        Result<LeadResponse, Error> result = createLeadUseCase.execute(request);

        assertFalse(result.isSuccess());
        assertEquals("El formato del email es inválido.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el telefono es null")
    void telefonoNuloRetornaError() {
        CreateLeadRequest request = new CreateLeadRequest(
                "Pepe", "pepe.montana@gmail.com", null, "Info", LeadSource.WEB, propertyId);

        Result<LeadResponse, Error> result = createLeadUseCase.execute(request);

        assertFalse(result.isSuccess());
        assertEquals("El teléfono es obligatorio.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el telefono esta vacio")
    void telefonoVacioRetornaError() {
        CreateLeadRequest request = new CreateLeadRequest(
                "Pepe", "pepe.montana@gmail.com", "", "Info", LeadSource.WEB, propertyId);

        Result<LeadResponse, Error> result = createLeadUseCase.execute(request);

        assertFalse(result.isSuccess());
        assertEquals("El teléfono es obligatorio.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe fallar si el propertyId es null")
    void propertyIdNuloRetornaError() {
        CreateLeadRequest request = new CreateLeadRequest(
                "Pepe", "pepe.montana@gmail.com", "600123456", "Info", LeadSource.WEB, null);

        Result<LeadResponse, Error> result = createLeadUseCase.execute(request);

        assertFalse(result.isSuccess());
        assertEquals("La propiedad es obligatoria.", result.getErrorValue().get().getMessage());
    }

    @Test
    @DisplayName("Debe aceptar source FSBO")
    void crearConFuenteFsbo() {
        CreateLeadRequest request = new CreateLeadRequest(
                "Maria", "maria@test.com", "600123456", "Info", LeadSource.FSBO, propertyId);
        Lead leadGuardado = Lead.create("Maria", new Email("maria@test.com"),
                "600123456", "Info", LeadSource.FSBO, propertyId);

        when(leadRepository.saveLead(any(Lead.class))).thenReturn(leadGuardado);

        Result<LeadResponse, Error> result = createLeadUseCase.execute(request);

        assertTrue(result.isSuccess());
        verify(leadRepository, times(1)).saveLead(any(Lead.class));
    }
}
