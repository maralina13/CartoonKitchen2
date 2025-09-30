package com.cartoonkitchen.service;

import com.cartoonkitchen.entity.User;

public interface UserService {
    void saveUser(User user);
    User findByUsername(String username);
}
