package com.cartoonkitchen.dto;

import lombok.Data;

@Data
public class DishApiResponseDTO {
    private Long id;
    private String name;
    private String category;
    private String recipe;
}
