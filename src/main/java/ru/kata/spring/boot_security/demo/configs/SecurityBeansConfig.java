package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.service.PasswordService;

@Configuration
public class SecurityBeansConfig {
    @Bean
    public PasswordEncoder passwordEncoder(PasswordService passwordService) {
        return passwordService.getPasswordEncoder();
    }
}
