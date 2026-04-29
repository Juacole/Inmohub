package com.inmohub.fsbo.ingestor.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FsboIngestorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FsboIngestorServiceApplication.class, args);
	}

}
