package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserDao userDao;
    private final RoleDao roleDao;

    @Autowired
    public DataInitializer(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("🚀 Инициализация данных (БЕЗ ШИФРОВАНИЯ)...");

        // Создаём роли
        Role adminRole = createRoleIfNotExists("ROLE_ADMIN");
        Role userRole = createRoleIfNotExists("ROLE_USER");

        // Администратор
        if (!userDao.existsByEmail("admin@admin.com")) {
            User admin = new User("Администратор", 30, "admin@admin.com", "admin");
            admin.setRoles(Set.of(adminRole));
            userDao.save(admin);
            System.out.println("✅ Создан администратор: admin@admin.com / admin");
        }

        // Обычный пользователь
        if (!userDao.existsByEmail("user@user.com")) {
            User user = new User("Обычный пользователь", 25, "user@user.com", "user");
            user.setRoles(Set.of(userRole));
            userDao.save(user);
            System.out.println("✅ Создан пользователь: user@user.com / user");
        }

        // Тестовый пользователь (опционально)
        if (!userDao.existsByEmail("test@test.com")) {
            User test = new User("Тестовый пользователь", 28, "test@test.com", "test");
            test.setRoles(Set.of(userRole));
            userDao.save(test);
            System.out.println("✅ Создан тестовый пользователь: test@test.com / test");
        }
    }

    private Role createRoleIfNotExists(String roleName) {
        try {
            return roleDao.findByName(roleName);
        } catch (Exception e) {
            Role role = new Role(roleName);
            roleDao.save(role);
            System.out.println("✅ Создана роль: " + roleName);
            return role;
        }
    }
}
