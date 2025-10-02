package ru.kata.spring.boot_security.demo.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.PasswordService;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordService passwordService;

    @Autowired
    public DataInitializer(UserDao userDao,
                           RoleDao roleDao,
                           PasswordService passwordService) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordService = passwordService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Starting data initialization...");
        Role adminRole = createRoleIfNotExists("ROLE_ADMIN");
        Role userRole = createRoleIfNotExists("ROLE_USER");

        createUserIfNotExists("Admin User", 30, "admin@admin.com", "admin", Set.of(adminRole));
        createUserIfNotExists("Regular User", 25, "user@user.com", "user", Set.of(userRole));
        createUserIfNotExists("Test User", 28, "test@test.com", "test", Set.of(userRole));

        logger.info("Data initialization completed successfully");
    }

    private Role createRoleIfNotExists(String roleName) {
        try {
            Role r = roleDao.findByName(roleName);
            if (r != null) return r;
        } catch (Exception ignored) {
        }
        Role role = new Role(roleName);
        roleDao.save(role);
        return role;
    }

    private void createUserIfNotExists(String name, int age, String email, String pwd, Set<Role> roles) {
        if (!userDao.existsByEmail(email)) {
            String hash = passwordService.getDefaultPasswordHash(pwd);
            if (hash == null) hash = passwordService.encodePassword(pwd);
            User u = new User(name, age, email, hash);
            u.setRoles(roles);
            userDao.save(u);
            logger.info("Created user: {} <{}>", name, email);
        }
    }
}
