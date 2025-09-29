package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;

public interface RoleDao {

    List<Role> findAll();

    Role findById(long id);

    Role findByName(String name);

    void save(Role role);

    void deleteById(Long id);
}
