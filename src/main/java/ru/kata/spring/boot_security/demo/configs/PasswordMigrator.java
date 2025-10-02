package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.PasswordService;

import java.util.List;

@Component
public class PasswordMigrator implements CommandLineRunner {

    private final UserDao userDao;
    private final PasswordService passwordService;

    @Autowired
    public PasswordMigrator(UserDao userDao, PasswordService passwordService) {
        this.userDao = userDao;
        this.passwordService = passwordService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<User> users = userDao.findAll();
        for (User u : users) {
            if (!passwordService.isPasswordEncoded(u.getPassword())) {
                u.setPassword(passwordService.encodePassword(u.getPassword()));
                userDao.update(u);
                System.out.println("Password migrated for " + u.getEmail());
            }
        }
    }
}
