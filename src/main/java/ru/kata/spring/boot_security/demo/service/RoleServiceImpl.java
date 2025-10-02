package ru.kata.spring.boot_security.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final RoleDao roleDao;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public List<Role> getAllRoles() {
        logger.debug("Getting all roles");
        List<Role> roles = roleDao.findAll();
        logger.debug("Retrieved {} roles", roles.size());
        return roles;
    }

    @Override
    public Role getRoleById(Long id) {
        logger.debug("Getting role by id: {}", id);
        if (id == null) {
            logger.warn("Role ID cannot be null");
            return null;
        }
        Role role = roleDao.findById(id);
        if (role != null) {
            logger.debug("Found role: {}", role.getName());
        } else {
            logger.debug("Role not found with id: {}", id);
        }
        return role;
    }

    @Override
    public Role getRoleByName(String name) {
        logger.debug("Getting role by name: {}", name);
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Role name cannot be null or empty");
            return null;
        }
        Role role = roleDao.findByName(name.trim());
        if (role != null) {
            logger.debug("Found role: {} with id: {}", role.getName(), role.getId());
        } else {
            logger.warn("Role not found with name: {}", name);
        }
        return role;
    }
}
