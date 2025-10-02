package ru.kata.spring.boot_security.demo.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<User> findAll() {
        logger.debug("Finding all users without roles");
        return entityManager.createQuery("select u from User u", User.class).getResultList();
    }

    @Override
    public List<User> findAllWithRoles() {
        logger.debug("Finding all users with roles");
        return entityManager.createQuery("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles",
                User.class).getResultList();
    }

    @Override
    public Optional<User> findById(Long id) {
        logger.debug("Finding user with id {}", id);
        if (id == null) {
            logger.warn("User not found");
            return Optional.empty();
        }
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByIdWithRoles(Long id) {
        logger.debug("Finding user with id {}", id);
        if (id == null) {
            logger.warn("User not found");
            return Optional.empty();
        }
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id", User.class);
            query.setParameter("id", id);

            User user = query.getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            logger.debug("User with id {} not found", id);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("User with id {} not found", id);
            return Optional.empty();
        }
    }

    @Override
    public void save(User user) {
        logger.debug("Saving user with email: {}", user.getEmail());
        if (user == null) {
            logger.error("Attempted to save null user");
            throw new IllegalArgumentException("User cannot be null");
        }
        try {
            entityManager.persist(user);
            logger.info("User with email {} saved", user.getEmail());
        } catch (Exception e) {
            logger.error("Error saving user with email: {}", user.getEmail(), e);
            throw e;
        }
    }

    @Override
    public void update(User user) {
        logger.debug("Updating user with id: {} and email: {}", user.getId(), user.getEmail());
        if (user == null) {
            logger.error("Attempted to update null user");
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getId() == null) {
            logger.error("Attempted to update user without ID");
            throw new IllegalArgumentException("User ID cannot be null for update");
        }
        try {
            entityManager.merge(user);
            logger.info("Successfully updated user with id: {}", user.getId());
        } catch (Exception e) {
            logger.error("Error updating user with id: {}", user.getId(), e);
            throw e;
        }
    }


    @Override
    public void delete(Long id) {
        logger.debug("Deleting user with id: {}", id);
        if (id == null) {
            logger.warn("Attempted to delete user with null id");
            return;
        }
        try {
            Optional<User> user = findById(id);

            if (user.isPresent()) {
                User userToDelete = user.get();
                entityManager.remove(userToDelete);
                logger.info("Successfully deleted user with id: {}", id);
            } else {
                logger.warn("User with id {} not found for deletion", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting user with id: {}", id, e);
            throw e;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user with email {}", email);
        if (email == null || email.isEmpty()) {
            logger.warn("User with email {} not found", email);
            return Optional.empty();
        }
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email.trim());

            User user = query.getSingleResult();
            return Optional.of(user);

        } catch (NoResultException e) {
            logger.debug("User not found with email: {}", email);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error finding user by email: {}\"", email);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmailWithRoles(String email) {
        logger.debug("Finding user by email with role: {}", email);
        if (email == null || email.trim().isEmpty()) {
            logger.warn("User with email {} not found", email);
            return Optional.empty();
        }
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email", User.class);
            query.setParameter("email", email.trim());

            User user = query.getSingleResult();
            return Optional.of(user);

        } catch (NoResultException e) {
            logger.debug("User not found with email: {}", email);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error finding user by email with roles: {}", email);
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        logger.debug("Checking if user with email {}", email);
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Checking if user exists with email: {}", email);
            return false;
        }
        try {
            Long count = entityManager.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email.trim())
                    .getSingleResult();

            boolean exists = count > 0;
            logger.debug("User with email {} exists {}", email, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking existence of user with email: {}", email, e);
            return false;
        }
    }
}