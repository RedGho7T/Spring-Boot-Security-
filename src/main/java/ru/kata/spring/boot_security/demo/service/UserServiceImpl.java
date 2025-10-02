package ru.kata.spring.boot_security.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final PasswordService passwordService;

    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordService passwordService) {
        this.userDao = userDao;
        this.passwordService = passwordService;
    }

    @Override
    public List<User> getAllUsers() {
        logger.debug("Getting all users without roles");

        try {
            List<User> users = userDao.findAll();
            logger.info("Retrieved {} users without roles", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error retrieving all users", e);
            throw new RuntimeException("Failed to get all users", e);
        }
    }

    @Override
    public List<User> getAllUsersWithRoles() {
        logger.debug("Getting all users with roles");

        try {
            List<User> users = userDao.findAllWithRoles();
            logger.info("Retrieved {} users with roles", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error retrieving all users with roles", e);
            throw new RuntimeException("Failed to get all users with roles", e);
        }
    }

    @Override
    public Optional<User> getUserById(Long id) {
        logger.debug("Getting user by id: {}", id);

        if (id == null) {
            logger.warn("Attempted to get user with null id", id);
            return Optional.empty();
        }
        try {
            Optional<User> user = userDao.findById(id);
            if (user.isPresent()) {
                logger.debug("Found user with id: {}", id);
            } else {
                logger.debug("User not found with id: {}", id);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error getting user by id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> getUserByIdWithRoles(Long id) {
        logger.debug("Getting user by id with roles: {}", id);

        if (id == null) {
            logger.warn("Attempted to get user with null id", id);
            return Optional.empty();
        }
        try {
            Optional<User> user = userDao.findByIdWithRoles(id);
            if (user.isPresent()) {
                logger.debug("Found user with id {} and {} roles", id, user.get().getRoles().size());
            } else {
                logger.debug("User not found with id: {}", id);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error getting user by id with roles: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public void saveUser(User user) {
        logger.debug("Attempting to save user with email: {}",
                user != null ? user.getEmail() : "null");
        validateUserForSave(user);
        encodeUserPassword(user);

        try {
            userDao.save(user);
            logger.debug("User saved with email: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Error saving user with email: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        logger.debug("Attempting to delete user with id: {}", id);

        if (id == null) {
            logger.warn("Attempted to delete user with null id");
            return;
        }
        try {
            userDao.delete(id);
            logger.info("Successfully deleted user with id: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting user with id: {}", id, e);
            throw new RuntimeException("Failed to delete user with id: " + id, e);
        }

    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.warn("Attempted to find user with null or empty email");
            return Optional.empty();
        }
        try {
            Optional<User> user = userDao.findByEmail(email);
            if (user.isPresent()) {
                logger.debug("Found user with email: {}", email);
            } else {
                logger.debug("User not found with email: {}", email);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error getting user by email: {}", email, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmailWithRoles(String email) {

        if (email == null || email.trim().isEmpty()) {
            logger.warn("Attempted to find user with null or empty email");
            return Optional.empty();
        }
        try {
            Optional<User> user = userDao.findByEmailWithRoles(email);

            if (user.isPresent()) {
                logger.debug("Found user with email: {} and {} roles",
                        email, user.get().getRoles().size());
            } else {
                logger.debug("User not found with email: {}", email);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error finding user by email with roles: {}", email, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        logger.debug("Checking if user with email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.warn("Attempted to check user with null or empty email");
            return false;
        }
        try {
            boolean exists = userDao.existsByEmail(email);
            logger.debug("User with email {} exists: {}", email, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking user existence by email: {}", email, e);
            return false;
        }
    }

    @Override
    public void updateUser(User user) {
        logger.debug("Attempting to update user with id: {} and email: {}",
                user.getId(), user.getEmail());

        validateUserForUpdate(user);

        Optional<User> existingUser = userDao.findByIdWithRoles(user.getId());
        if (!existingUser.isPresent()) {
            logger.error("User with id {} not found for update", user.getId());
            throw new IllegalArgumentException("User with id " + user.getId() + " not found");
        }
        User existUser = existingUser.get();
        logger.debug("Found existing user: {}", (existUser.getEmail()));

        handlePasswordUpdate(user, existUser);
        handleRolesUpdate(user, existUser);

        try {
            userDao.update(user);
            logger.info("Successfully updated user with id: {} and email: {}",
                    user.getId(), user.getEmail());
        } catch (Exception e) {
            logger.error("Error updating user with id: {}", user.getId(), e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    private void validateUserForSave(User user) {

        if (user == null) {
            logger.error("Cannot save null user");
            throw new IllegalArgumentException("User must not be null");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            logger.error("Cannot save user without email");
            throw new IllegalArgumentException("User email must not be null or empty");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            logger.error("Cannot save user without password");
            throw new IllegalArgumentException("Password must not be null or empty");
        }
        if (existsByEmail(user.getEmail())) {
            logger.error("User with email {} already exists", user.getEmail());
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
    }

    private void encodeUserPassword(User user) {
        String raw = user.getPassword().trim();
        user.setPassword(passwordService.encodePassword(raw));
    }


    private void validateUserForUpdate(User user) {
        if (user == null) {
            logger.error("Cannot update null user");
            throw new IllegalArgumentException("User must not be null");
        }
        if (user.getId() == null) {
            logger.error("Cannot update user without ID");
            throw new IllegalArgumentException("User ID must not be null for update");
        }
    }

    private void handlePasswordUpdate(User newUser, User existUser) {
        String np = newUser.getPassword();
        if (np == null || np.trim().isEmpty()) {
            newUser.setPassword(existUser.getPassword());
            return;
        }
        if (passwordService.isPasswordEncoded(np)) {
            return;
        }
        newUser.setPassword(passwordService.encodePassword(np.trim()));
    }

    private void handleRolesUpdate(User newUser, User existUser) {
        Set<Role> newRoles = newUser.getRoles();

        if (newRoles == null || newRoles.isEmpty()) {
            logger.debug("No roles provided, keeping existing roles for user: {}",
                    existUser.getEmail());
            newUser.setRoles(existUser.getRoles());
            return;
        }
        logger.debug("Setting new roles for user: {}. Roles count: {}",
                existUser.getEmail(), newRoles.size());
    }
}