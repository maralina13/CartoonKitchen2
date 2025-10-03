package com.cartoonkitchen.service;

import com.cartoonkitchen.entity.Dish;
import com.cartoonkitchen.dto.DishRequestDTO;
import com.cartoonkitchen.entity.User;

import java.io.IOException;

import java.util.List;
import java.util.Optional;

public interface DishService {
    List<Dish> findAll();
    void saveDish(Dish dish);
    void saveFullDish(DishRequestDTO dto) throws IOException;
    Optional<Dish> findById(Long id);
    void deleteById(Long id);
    void update(Long id, com.cartoonkitchen.dto.DishRequestDTO form);
    List<Dish> findAllByAuthor(User user);

}
