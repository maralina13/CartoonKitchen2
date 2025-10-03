// FavoriteController.java
package com.cartoonkitchen.controller;

import com.cartoonkitchen.repository.FavoriteRepository;
import com.cartoonkitchen.repository.UserRepository;
import com.cartoonkitchen.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/toggle")
    public String toggle(Authentication auth,
                         @RequestParam("dishId") Long dishId,
                         @RequestParam(value = "back", defaultValue = "/") String back) {

        var user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден: " + auth.getName()));

        favoriteService.toggle(user.getId(), dishId);
        return "redirect:" + back;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public String myFavorites(Authentication auth, Model model) {
        var user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден: " + auth.getName()));

        var dishes = favoriteService.findAllByUserId(user.getId());
        model.addAttribute("dishes", dishes);
        model.addAttribute("title", "Мои избранные");
        return "favorites";
    }
}
