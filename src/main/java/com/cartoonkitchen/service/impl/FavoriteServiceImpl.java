package com.cartoonkitchen.service.impl;

import com.cartoonkitchen.entity.Dish;
import com.cartoonkitchen.entity.Favorite;
import com.cartoonkitchen.repository.DishRepository;
import com.cartoonkitchen.repository.FavoriteRepository;
import com.cartoonkitchen.repository.UserRepository;
import com.cartoonkitchen.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final DishRepository dishRepository;

    @Override
    @Transactional
    public void toggle(Long userId, Long dishId) {
        var favoriteOpt = favoriteRepository.findByUser_IdAndDish_Id(userId, dishId);
        if (favoriteOpt.isPresent()) {
            favoriteRepository.delete(favoriteOpt.get());
        } else {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userId));
            var dish = dishRepository.findById(dishId)
                    .orElseThrow(() -> new IllegalArgumentException("Блюдо не найдено: " + dishId));
            Favorite fav = new Favorite();
            fav.setUser(user);
            fav.setDish(dish);
            favoriteRepository.save(fav);
        }
    }

    @Override
    public List<Dish> findAllByUserId(Long userId) {
        return favoriteRepository.findAllByUser_Id(userId)
                .stream()
                .map(Favorite::getDish)
                .toList();
    }

    @Override
    public boolean isFavorite(Long userId, Long dishId) {
        return favoriteRepository.findByUser_IdAndDish_Id(userId, dishId).isPresent();
    }
}
