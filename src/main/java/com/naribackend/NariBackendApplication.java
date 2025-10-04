package com.naribackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NariBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NariBackendApplication.class, args);
    }

}
