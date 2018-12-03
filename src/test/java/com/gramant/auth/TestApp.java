package com.gramant.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootApplication
@Import(AuthConfiguration.class)
@ActiveProfiles("test")
public class TestApp {

    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }
}
