package com.cartoonkitchen.service;

import com.cartoonkitchen.entity.Category;

import java.util.List;

public interface CategoryService {
    Category findByName(String name);
    void saveCategory(Category category);
    List<Category> findAll();
}
