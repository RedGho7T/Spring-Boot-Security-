package ru.kata.spring.boot_security.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.Set;

@Controller
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public RegisterController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/register")
    public String showForm(Model m) {
        m.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Valid User user,
                           BindingResult br, Model model) {
        if (br.hasErrors()) return "register";
        try {
            Role r = roleService.getRoleByName("ROLE_USER");
            user.setRoles(Set.of(r));
            userService.saveUser(user);
            logger.info("Registered new user: {}", user.getEmail());
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("emailError", e.getMessage());
            return "register";
        }
    }
}
