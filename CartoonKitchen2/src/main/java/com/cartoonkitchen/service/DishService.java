package com.cartoonkitchen.service;

import com.cartoonkitchen.entity.Dish;
import com.cartoonkitchen.dto.DishRequestDTO;
import java.io.IOException;

import java.util.List;

public interface DishService {
    List<Dish> findAll();
    void saveDish(Dish dish);
    void saveFullDish(DishRequestDTO dto) throws IOException;
    void saveDishWithSteps(
            String name,
            String categoryName,
            String recipe,
            byte[] finalImage,
            byte[] cartoonImage,
            List<String> stepDescriptions,
            List<byte[]> stepImages
    );

}
