package com.cram.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CramBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(CramBackendApplication.class, args);
    }

}
