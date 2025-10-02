package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    List<User> findAll();

    List<User> findAllWithRoles();


    Optional<User> findById(Long id);

    Optional<User> findByIdWithRoles(Long id);


    void save(User user);

    void update(User user);

    void delete(Long id);


    Optional<User> findByEmail(String email);

    Optional<User> findByEmailWithRoles(String email);

    boolean existsByEmail(String email);
}