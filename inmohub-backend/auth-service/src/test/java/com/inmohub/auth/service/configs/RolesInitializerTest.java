package com.inmohub.auth.service.configs;

import com.inmohub.auth.service.models.Role;
import com.inmohub.auth.service.repositories.IRoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para {@link RolesInitializer}.
 * Verifica que el inicializador solo cree los roles por defecto
 * (ADMIN, AGENT, OWNER, CLIENT) cuando no existen en base de datos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RolesInitializer")
class RolesInitializerTest {

    @Mock
    private IRoleRepository roleRepository;

    @InjectMocks
    private RolesInitializer rolesInitializer;

    @Test
    @DisplayName("Debe crear roles por defecto si no existen en BD")
    void inicializarRolesNuevos() throws Exception {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        rolesInitializer.initRoles().run();

        verify(roleRepository, times(4)).save(any(Role.class));
    }

    @Test
    @DisplayName("No debe crear roles que ya existen en BD")
    void noCrearRolesExistentes() throws Exception {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(new Role()));

        rolesInitializer.initRoles().run();

        verify(roleRepository, never()).save(any(Role.class));
    }
}
