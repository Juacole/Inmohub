package com.inmohub.lead.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Clase principal de la aplicacion del microservicio de leads.
 * Habilita la auditoria automatica de JPA para el registro de fechas de creacion y modificacion.
 */
@SpringBootApplication
@EnableJpaAuditing
public class LeadServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeadServiceApplication.class, args);
    }

}
