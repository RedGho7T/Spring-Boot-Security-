package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final Map<String, String> defaultPasswordsCache;

    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder(12);
        this.defaultPasswordsCache = new ConcurrentHashMap<>();
        initializeDefaultPasswords();
    }

    private void initializeDefaultPasswords() {
        defaultPasswordsCache.put("admin", passwordEncoder.encode("admin"));
        defaultPasswordsCache.put("user", passwordEncoder.encode("user"));
        defaultPasswordsCache.put("test", passwordEncoder.encode("test"));
    }

    public String getDefaultPasswordHash(String plainPassword) {
        return defaultPasswordsCache.get(plainPassword);
    }

    public boolean isDefaultPassword(String plainPassword) {
        return defaultPasswordsCache.containsKey(plainPassword);
    }

    public String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        String trimmed = rawPassword.trim();
        if (isDefaultPassword(trimmed)) {
            return getDefaultPasswordHash(trimmed);
        }
        return passwordEncoder.encode(trimmed);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public boolean isPasswordEncoded(String password) {
        return password != null && password.startsWith("$2a$");
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
