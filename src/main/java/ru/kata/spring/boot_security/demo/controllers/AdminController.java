package ru.kata.spring.boot_security.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsersWithRoles());
        return "admin/list";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/new";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") User user, Model model) {
        try {
            userService.saveUser(user);
            return "redirect:/admin";
        } catch (Exception e) {
            logger.error("Error creating user", e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserByIdWithRoles(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/edit";
    }


    @PostMapping("/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("user") User user,
                             @RequestParam(value = "roleIds", required = false) Set<Long> roleIds,
                             Model model) {
        try {
            user.setId(id);
            if (roleIds != null) {
                Set<Role> roles = roleService.getAllRoles().stream()
                        .filter(r -> roleIds.contains(r.getId()))
                        .collect(Collectors.toSet());
                user.setRoles(roles);
            }
            userService.updateUser(user);
            return "redirect:/admin";
        } catch (Exception e) {
            logger.error("Error updating user", e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteUser(id);
            return "redirect:/admin";
        } catch (Exception e) {
            logger.error("Error deleting user", e);
        }
        return "redirect:/admin";
    }
}