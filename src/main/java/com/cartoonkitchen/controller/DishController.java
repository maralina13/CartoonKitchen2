package com.cartoonkitchen.controller;

import com.cartoonkitchen.dto.DishRequestDTO;
import com.cartoonkitchen.entity.Dish;
import com.cartoonkitchen.repository.DishRepository;
import com.cartoonkitchen.repository.IngredientRepository;
import com.cartoonkitchen.repository.UserRepository;
import com.cartoonkitchen.service.DishService;
import com.cartoonkitchen.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;
    private final IngredientRepository ingredientRepository;
    private final DishRepository dishRepository;
    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @PostMapping("/add")
    public String addDish(@ModelAttribute("dishDto") @Valid DishRequestDTO dishDto,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ingredients", ingredientRepository.findAll());
            return "add-dish";
        }

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
        if (!model.containsAttribute("dishDto")) {
            model.addAttribute("dishDto", new DishRequestDTO());
        }
        model.addAttribute("hideTopBar", true);
        return "add-dish";
    }

    @GetMapping("/{id:\\d+}")
    public String viewDish(@PathVariable Long id, Authentication auth, Model model) {
        Dish dish = dishService.findById(id).orElseThrow(() -> new RuntimeException("Блюдо не найдено"));
        model.addAttribute("dish", dish);

        boolean isFavorite = false;
        boolean canEdit = false;

        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            var user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                isFavorite = favoriteService.isFavorite(user.getId(), id);
                canEdit = dish.getAuthor() != null
                        && username.equals(dish.getAuthor().getUsername())
                        || auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            }
        }

        model.addAttribute("isFavorite", isFavorite);
        model.addAttribute("canEdit", canEdit);
        return "dish-view";
    }


    @PreAuthorize("@dishSecurity.isOwner(#id, authentication) or hasRole('ADMIN')")
    @PostMapping("/delete/{id:\\d+}")
    public String deleteDish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        dishService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Блюдо удалено");
        return "redirect:/";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id:\\d+}/edit")
    public String editDishForm(@PathVariable Long id, Model model) {
        var dish = dishService.findById(id)
                .orElseThrow(() -> new RuntimeException("Блюдо не найдено"));

        DishRequestDTO form = new DishRequestDTO();
        form.setName(dish.getName());
        form.setCategoryName(dish.getCategory() != null ? dish.getCategory().getName() : "");
        form.setFinalImageUrl(dish.getImageUrl());
        form.setCartoonImageUrl(dish.getCartoonImageUrl());

        if (dish.getRecipe() != null && !dish.getRecipe().isBlank()) {
            form.setStepDescriptions(java.util.Arrays.stream(dish.getRecipe().split("\\r?\\n"))
                    .map(String::trim).filter(s -> !s.isEmpty()).toList());
        } else {
            form.setStepDescriptions(java.util.List.of(""));
        }

        model.addAttribute("form", form);
        model.addAttribute("id", id);
        return "dish-edit";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/{id}/edit")
    public String updateDish(@PathVariable Long id,
                             @ModelAttribute("form") @Valid DishRequestDTO form,
                             BindingResult br,
                             RedirectAttributes ra,
                             Model model) {

        if (form.getStepDescriptions() != null
                && form.getStepDescriptions().size() == 1
                && form.getStepDescriptions().get(0) != null
                && form.getStepDescriptions().get(0).contains("\n")) {
            var split = java.util.Arrays.stream(form.getStepDescriptions().get(0).split("\\r?\\n"))
                    .map(String::trim).filter(s -> !s.isEmpty()).toList();
            form.setStepDescriptions(split);
        }

        if (br.hasErrors()) {
            model.addAttribute("id", id);
            return "dish-edit";
        }

        dishService.update(id, form);
        ra.addFlashAttribute("message", "Блюдо обновлено");
        return "redirect:/dishes/" + id;
    }


    @GetMapping("/search/by-ingredients")
    public String searchByIngredients(@RequestParam List<Long> ingredientIds, Model model) {
        var dishes = dishRepository.findByAllIngredientIds(ingredientIds, ingredientIds.size());
        model.addAttribute("dishes", dishes);
        return "dish-list";
    }

    @GetMapping("/top/steps")
    @ResponseBody
    public List<Map<String, Object>> topDetailed(@RequestParam(defaultValue = "5") int size) {
        return dishRepository
                .findTopByStepsCount(PageRequest.of(0, size))
                .getContent()
                .stream()
                .map(d -> Map.<String, Object>of(
                        "id", (Object) d.getId(),
                        "name", d.getName()
                ))
                .toList();
    }
}
