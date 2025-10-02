package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    List<User> getAllUsersWithRoles();

    Optional<User> getUserById(Long id);

    Optional<User> getUserByIdWithRoles(Long id);

    void saveUser(User user);

    void deleteUser(Long id);

    void updateUser(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailWithRoles(String email);

    boolean existsByEmail(String email);
}