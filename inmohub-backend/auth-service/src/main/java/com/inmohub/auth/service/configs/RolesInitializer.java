package com.inmohub.auth.service.configs;

import com.inmohub.auth.service.models.Role;
import com.inmohub.auth.service.repositories.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Inicializador de roles por defecto en la base de datos al arrancar la aplicacion.
 * Inserta los roles ADMIN, AGENT, OWNER y CLIENT si no existen.
 */
@Configuration
@RequiredArgsConstructor
public class RolesInitializer {
    private final IRoleRepository roleRepository;

    @Bean
    public CommandLineRunner initRoles() {
        return args -> {
            List<String> defaultRoles = List.of("ADMIN", "AGENT", "OWNER", "CLIENT");

            for (String roleName : defaultRoles) {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    Role role = new Role();
                    role.setName(roleName);
                    role.setDescription("Rol base del sistema: " + roleName);
                    roleRepository.save(role);

                    System.out.println("Rol inicializado en BD: " + roleName);
                }
            }
        };
    }
}