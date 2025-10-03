package com.cartoonkitchen.controller;

import com.cartoonkitchen.entity.Dish;
import com.cartoonkitchen.repository.DishRepository;
import com.cartoonkitchen.service.CategoryService;
import com.cartoonkitchen.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CategoryService categoryService;
    private final DishService dishService;
    private final DishRepository dishRepository;

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "") String query,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "8") int size,
                        @RequestParam(defaultValue = "createdAt,desc") String sort,
                        Model model) {

        String[] parts = sort.split(",", 2);
        Sort.Direction dir = (parts.length == 2 && parts[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort s = Sort.by(dir, parts[0]);

        Pageable pageable = PageRequest.of(page, size, s);

        Page<Dish> dishes = query.isBlank()
                ? dishRepository.findAll(pageable)
                : dishRepository.findByNameContainingIgnoreCase(query, pageable);

        model.addAttribute("dishes", dishes);
        model.addAttribute("query", query);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);

        return "home";
    }
}
