package com.inmohub.lead.service.domain.model;

import com.inmohub.lead.service.domain.abstractions.DomainException;
import com.inmohub.lead.service.domain.model.enums.LeadSource;
import com.inmohub.lead.service.domain.model.enums.LeadStatus;
import com.inmohub.lead.service.domain.valueobjetcs.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para la entidad de dominio {@link Lead}.
 * Cubre creacion, validaciones de campos obligatorios,
 * transiciones de estado (contactLead, closeLead, updateStatus)
 * y reconstituicion desde persistencia.
 */
@DisplayName("Lead (Modelo de Dominio)")
class LeadTest {

    private final Email emailValido = new Email("pepe.montana@gmail.com");
    private final UUID propertyId = UUID.randomUUID();

    @Nested
    @DisplayName("Creación de Lead")
    class Creacion {

        @Test
        @DisplayName("Debe crear un Lead con estado inicial NEW")
        void crearLeadExitoso() {
            Lead lead = Lead.create("Pepe Montana", emailValido, "600123456", "Quiero info", LeadSource.WEB, propertyId);

            assertNotNull(lead.getId());
            assertEquals("Pepe Montana", lead.getName());
            assertEquals("pepe.montana@gmail.com", lead.getEmail().value());
            assertEquals("600123456", lead.getPhone());
            assertEquals("Quiero info", lead.getMessage());
            assertEquals(LeadSource.WEB, lead.getSource());
            assertEquals(propertyId, lead.getPropertyId());
            assertEquals(LeadStatus.NEW, lead.getStatus());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el nombre es null")
        void nombreNuloLanzaExcepcion() {
            DomainException exception = assertThrows(DomainException.class, () -> {
                Lead.create(null, emailValido, "600123456", "Mensaje", LeadSource.WEB, propertyId);
            });
            assertEquals("El nombre del lead no puede estar vacío.", exception.getMessage());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el nombre está vacío")
        void nombreVacioLanzaExcepcion() {
            DomainException exception = assertThrows(DomainException.class, () -> {
                Lead.create("", emailValido, "600123456", "Mensaje", LeadSource.WEB, propertyId);
            });
            assertEquals("El nombre del lead no puede estar vacío.", exception.getMessage());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el email es null")
        void emailNuloLanzaExcepcion() {
            assertThrows(NullPointerException.class, () -> {
                Lead.create("Pepe", null, "600123456", "Mensaje", LeadSource.WEB, propertyId);
            });
        }

        @Test
        @DisplayName("Debe lanzar excepción si el teléfono es null")
        void telefonoNuloLanzaExcepcion() {
            DomainException exception = assertThrows(DomainException.class, () -> {
                Lead.create("Pepe", emailValido, null, "Mensaje", LeadSource.WEB, propertyId);
            });
            assertEquals("El teléfono es obligatorio para contactar al lead.", exception.getMessage());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el teléfono está vacío")
        void telefonoVacioLanzaExcepcion() {
            DomainException exception = assertThrows(DomainException.class, () -> {
                Lead.create("Pepe", emailValido, "", "Mensaje", LeadSource.WEB, propertyId);
            });
            assertEquals("El teléfono es obligatorio para contactar al lead.", exception.getMessage());
        }

        @Test
        @DisplayName("Debe permitir mensaje null y asignar cadena vacía")
        void mensajeNuloAsignaVacio() {
            Lead lead = Lead.create("Pepe", emailValido, "600123456", null, LeadSource.WEB, propertyId);
            assertEquals("", lead.getMessage());
        }

        @Test
        @DisplayName("Debe aceptar LeadSource FSBO")
        void crearConFuenteFsbo() {
            Lead lead = Lead.create("Maria Perez", emailValido, "600123456", "Info", LeadSource.FSBO, propertyId);
            assertEquals(LeadSource.FSBO, lead.getSource());
        }
    }

    @Nested
    @DisplayName("Cambio de estado")
    class CambioEstado {

        @Test
        @DisplayName("contactLead debe cambiar estado de NEW a CONTACTED")
        void contactarLeadExitoso() {
            Lead lead = Lead.create("Pepe", emailValido, "600123456", "Mensaje", LeadSource.WEB, propertyId);
            lead.contactLead();
            assertEquals(LeadStatus.CONTACTED, lead.getStatus());
        }

        @Test
        @DisplayName("contactLead debe lanzar excepción si el lead está CLOSED")
        void contactarLeadCerradoLanzaExcepcion() {
            Lead lead = Lead.create("Pepe", emailValido, "600123456", "Mensaje", LeadSource.WEB, propertyId);
            lead.closeLead();
            DomainException exception = assertThrows(DomainException.class, lead::contactLead);
            assertTrue(exception.getMessage().contains("No se puede contactar un lead cerrado o perdido"));
        }

        @Test
        @DisplayName("contactLead debe lanzar excepción si el lead está LOST")
        void contactarLeadPerdidoLanzaExcepcion() {
            Lead lead = Lead.create("Pepe", emailValido, "600123456", "Mensaje", LeadSource.WEB, propertyId);
            lead.updateStatus(LeadStatus.LOST);
            DomainException exception = assertThrows(DomainException.class, lead::contactLead);
            assertTrue(exception.getMessage().contains("No se puede contactar un lead cerrado o perdido"));
        }

        @Test
        @DisplayName("closeLead debe cambiar estado a CLOSED")
        void cerrarLeadExitoso() {
            Lead lead = Lead.create("Pepe", emailValido, "600123456", "Mensaje", LeadSource.WEB, propertyId);
            lead.closeLead();
            assertEquals(LeadStatus.CLOSED, lead.getStatus());
        }

        @Test
        @DisplayName("updateStatus debe actualizar al nuevo estado")
        void actualizarEstadoExitoso() {
            Lead lead = Lead.create("Pepe", emailValido, "600123456", "Mensaje", LeadSource.WEB, propertyId);
            lead.updateStatus(LeadStatus.NEGOTIATION);
            assertEquals(LeadStatus.NEGOTIATION, lead.getStatus());
        }

        @Test
        @DisplayName("updateStatus debe lanzar excepción si el nuevo estado es null")
        void actualizarEstadoNuloLanzaExcepcion() {
            Lead lead = Lead.create("Pepe", emailValido, "600123456", "Mensaje", LeadSource.WEB, propertyId);
            assertThrows(NullPointerException.class, () -> lead.updateStatus(null));
        }
    }

    @Nested
    @DisplayName("Reconstitución")
    class Reconstitucion {

        @Test
        @DisplayName("Debe reconstituir un Lead con los datos proporcionados")
        void reconstituirLead() {
            UUID id = UUID.randomUUID();
            Lead lead = Lead.reconstitute(id, "Pepe", emailValido, "600123456", "Mensaje",
                    LeadSource.MANUAL, propertyId, LeadStatus.CONTACTED);

            assertEquals(id, lead.getId());
            assertEquals("Pepe", lead.getName());
            assertEquals(LeadStatus.CONTACTED, lead.getStatus());
        }
    }
}
