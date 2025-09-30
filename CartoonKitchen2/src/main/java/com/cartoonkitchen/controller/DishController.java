package com.cartoonkitchen.controller;

import org.springframework.ui.Model;
import com.cartoonkitchen.dto.DishRequestDTO;
import com.cartoonkitchen.service.DishService;
import com.cartoonkitchen.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;
    private final IngredientRepository ingredientRepository;

    @PostMapping("/add")
    public String addDish(@ModelAttribute DishRequestDTO dishDto, RedirectAttributes redirectAttributes) {
        try {
            dishService.saveFullDish(dishDto);
            redirectAttributes.addFlashAttribute("message", "Блюдо добавлено!");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при сохранении блюда: " + e.getMessage());
            return "redirect:/dishes/add";
        }
    }

    @GetMapping("/add")
    public String showAddDishForm(Model model) {
        model.addAttribute("ingredients", ingredientRepository.findAll());
        return "add-dish";
    }

}
