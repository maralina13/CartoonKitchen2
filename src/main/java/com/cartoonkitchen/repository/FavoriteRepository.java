package com.cartoonkitchen.repository;

import com.cartoonkitchen.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByUser_Id(Long userId);
    Optional<Favorite> findByUser_IdAndDish_Id(Long userId, Long dishId);
}
