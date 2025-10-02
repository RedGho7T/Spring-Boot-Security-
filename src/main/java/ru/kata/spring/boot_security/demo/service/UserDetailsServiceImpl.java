package ru.kata.spring.boot_security.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Attempting to load user by email: {}", email);

        return userService.findByEmailWithRoles(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email)
                );
    }
}
