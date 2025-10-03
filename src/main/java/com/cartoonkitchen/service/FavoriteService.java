package com.cartoonkitchen.service;

import com.cartoonkitchen.entity.Dish;

import java.util.List;

public interface FavoriteService {
    void toggle(Long userId, Long dishId);
    boolean isFavorite(Long userId, Long dishId);
    List<Dish> findAllByUserId(Long userId);
}

