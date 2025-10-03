package com.cartoonkitchen.service;

import com.cartoonkitchen.entity.User;

import java.util.Optional;

public interface UserService {
    void saveUser(User user);
    Optional<User> findByUsername(String username);

    default User getByUsernameOrThrow(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден: " + username));
    }
}
