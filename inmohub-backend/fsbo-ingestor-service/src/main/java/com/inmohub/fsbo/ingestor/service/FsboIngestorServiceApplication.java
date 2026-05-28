package com.inmohub.fsbo.ingestor.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Punto de entrada del microservicio de ingestion FSBO.
 * Habilita JPA Auditing y clientes Feign para comunicacion con otros servicios.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
public class FsboIngestorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FsboIngestorServiceApplication.class, args);
	}

}
