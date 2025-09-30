package com.cartoonkitchen.service;

import com.cartoonkitchen.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category findByName(String name);
    void saveCategory(Category category);
}
