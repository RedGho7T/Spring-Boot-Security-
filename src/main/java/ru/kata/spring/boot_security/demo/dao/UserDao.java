package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findById(Long id);

    void save(User user);

    void update(User user);

    void delete(Long id);

    void deleteById(Long id);

    List<User> findByNameContaining(String name);

    boolean existsByEmail(String email);

    User findByEmail(String email);

    User getByEmail(String email);
}