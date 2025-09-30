package com.cartoonkitchen.controller;

import com.cartoonkitchen.service.CategoryService;
import com.cartoonkitchen.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CategoryService categoryService;
    private final DishService dishService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("dishes", dishService.findAll());
        return "home";
    }
}
