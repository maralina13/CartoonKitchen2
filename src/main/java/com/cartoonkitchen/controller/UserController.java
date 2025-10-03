package com.cartoonkitchen.controller;

import com.cartoonkitchen.entity.User;
import com.cartoonkitchen.repository.UserRepository;
import com.cartoonkitchen.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/authors/top")
    public String topAuthors(@RequestParam(defaultValue = "5") int size, Model model) {
        var page = userRepository.findTopAuthors(org.springframework.data.domain.PageRequest.of(0, size));
        model.addAttribute("authors", page.getContent());
        return "author-top";
    }
}
