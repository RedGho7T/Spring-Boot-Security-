package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void save(User user) {
        entityManager.persist(user);
    }

    @Override
    public void update(User user) {
        entityManager.merge(user);
    }

    @Override
    public void delete(Long id) {
        User user = findById(id);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    @Override
    public void deleteById(Long id) {
        delete(id);
    }

    @Override
    public List<User> findByNameContaining(String name) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.name LIKE :name", User.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            Long count = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            System.err.println("❌ Ошибка при проверке существования email: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User findByEmail(String email) {
        System.out.println("🔍 UserDaoImpl: Ищем пользователя с email: " + email);

        try {
            User user = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();

            System.out.println("✅ UserDaoImpl: Найден пользователь: " + user.getName());
            return user;

        } catch (NoResultException e) {
            System.err.println("❌ UserDaoImpl: Пользователь с email '" + email + "' НЕ НАЙДЕН");
            return null;
        } catch (Exception e) {
            System.err.println("❌ UserDaoImpl: Ошибка при поиске: " + e.getMessage());
            return null;
        }

    }

    @Override
    public User getByEmail(String email) {
        try {
            return entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}