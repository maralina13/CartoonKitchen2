package com.cartoonkitchen.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class DishRequestDTO {
    private String name;
    private String categoryName;

    private List<Long> ingredientIds;
    private List<String> newIngredients;

    private MultipartFile finalImage;
    private MultipartFile cartoonImage;

    private List<String> stepDescriptions;
    private List<MultipartFile> stepImages;
}
