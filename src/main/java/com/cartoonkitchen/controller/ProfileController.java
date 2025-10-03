package com.cartoonkitchen.controller;

import com.cartoonkitchen.service.DishService;
import com.cartoonkitchen.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final DishService dishService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public String myRecipes(Authentication auth, Model model) {
        String username = auth.getName();
        var user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден: " + username));

        var dishes = dishService.findAllByAuthor(user);

        model.addAttribute("dishes", dishes);
        model.addAttribute("title", "Мои рецепты");
        return "profile";
    }
}
