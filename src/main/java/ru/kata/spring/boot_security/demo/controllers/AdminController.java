package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String listUsers(Model model) {
        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            System.out.println("✅ Загружено пользователей: " + users.size());
            return "admin/list";
        } catch (Exception e) {
            System.out.println("❌ Ошибка при загрузке пользователей: " + e.getMessage());
            model.addAttribute("error", "Ошибка при загрузке списка пользователей");
            return "admin/list";
        }
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        try {
            model.addAttribute("user", new User());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/new";
        } catch (Exception e) {
            System.out.println("❌ Ошибка при загрузке формы создания: " + e.getMessage());
            model.addAttribute("error", "Ошибка при загрузке формы");
            return "admin/list";
        }
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") User user, Model model) {
        try {
            System.out.println("🔄 Создаём пользователя: " + user.getEmail());

            Set<Long> selectedRoleIds = user.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toSet());

            Set<Role> roles = roleService.getAllRoles().stream()
                    .filter(r -> selectedRoleIds.contains(r.getId()))
                    .collect(Collectors.toSet());

            user.setRoles(roles);

            userService.saveUser(user);

            System.out.println("✅ Пользователь создан успешно");
            return "redirect:/admin";

        } catch (Exception e) {
            System.out.println("❌ Ошибка при создании пользователя: " + e.getMessage());
            model.addAttribute("error", "Ошибка при создании пользователя: " + e.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                System.out.println("❌ Пользователь с ID " + id + " не найден");
                return "redirect:/admin";
            }

            model.addAttribute("user", user);
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/edit";

        } catch (Exception e) {
            System.out.println("❌ Ошибка при загрузке формы редактирования: " + e.getMessage());
            return "redirect:/admin";
        }
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("user") User user,
                             @RequestParam(value = "roleIds", required = false) Set<Long> roleIds,
                             Model model) {
        try {
            System.out.println("🔄 Обновляем пользователя с ID: " + id);

            User existingUser = userService.getUserById(id);
            if (existingUser == null) {
                System.out.println("❌ Пользователь с ID " + id + " не найден");
                return "redirect:/admin";
            }

            user.setId(id);

            if (roleIds != null && !roleIds.isEmpty()) {
                Set<Role> roles = roleService.getAllRoles().stream()
                        .filter(r -> roleIds.contains(r.getId()))
                        .collect(Collectors.toSet());
                user.setRoles(roles);
                System.out.println("✅ Установлены роли: " + roles.size());
            } else {
                user.setRoles(existingUser.getRoles());
                System.out.println("⚠️ Роли не изменились");
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword(existingUser.getPassword());
                System.out.println("⚠️ Пароль не изменился");
            } else {
                System.out.println("✅ Пароль обновлен");
            }

            userService.updateUser(user);
            System.out.println("✅ Пользователь обновлен успешно");

            return "redirect:/admin";

        } catch (Exception e) {
            System.out.println("❌ Ошибка при обновлении пользователя: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при обновлении пользователя: " + e.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id) {
        try {
            System.out.println("🗑️ Удаляем пользователя с ID: " + id);
            userService.deleteUser(id);
            System.out.println("✅ Пользователь удален успешно");
            return "redirect:/admin";
        } catch (Exception e) {
            System.out.println("❌ Ошибка при удалении пользователя: " + e.getMessage());
            return "redirect:/admin";
        }
    }
}