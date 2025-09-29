package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    void saveUser(User user);

    void updateUser(User user);

    void deleteUser(Long id);

    List<User> findByName(String name);

    boolean existsByEmail(String email);

    User getByEmail(String email);

    User findByEmail(String email);
}