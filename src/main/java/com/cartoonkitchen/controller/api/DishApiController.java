package com.cartoonkitchen.controller.api;

import com.cartoonkitchen.entity.Dish;
import com.cartoonkitchen.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishApiController {

    private final DishService dishService;

    record DishDTO(Long id, String name, String imageUrl, String cartoonImageUrl, String recipe) {
        public static DishDTO from(Dish dish) {
            return new DishDTO(dish.getId(), dish.getName(), dish.getImageUrl(), dish.getCartoonImageUrl(), dish.getRecipe());
        }
    }
}
