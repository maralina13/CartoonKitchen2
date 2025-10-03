package com.cartoonkitchen.controller;

import com.cartoonkitchen.entity.User;
import com.cartoonkitchen.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // СТРАНИЦА ВХОДА
    @GetMapping("/login")
    public String login(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }

    // ФОРМА РЕГИСТРАЦИИ
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ
    @PostMapping("/register")
    public String doRegister(@ModelAttribute("user") User user) {
        user.setRoles(Set.of("ROLE_USER"));
        userService.saveUser(user); // пароль хешируется в UserServiceImpl
        return "redirect:/login";
    }
}
