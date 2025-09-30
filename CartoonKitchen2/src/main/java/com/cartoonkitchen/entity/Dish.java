package com.cartoonkitchen.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "dishes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dish {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private Category category;

    @Lob
    private byte[] finalDishImage;

    @Lob
    private byte[] movieDishImage;

    @OneToMany(mappedBy = "dish", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Step> steps = new ArrayList<>();

    @Lob
    @Column(name = "image", nullable = false)
    private byte[] image;

    @Lob
    @Column(name = "cartoon_image", nullable = false)
    private byte[] cartoonImage;

    @ManyToMany
    @JoinTable(
            name = "dish_ingredients",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String recipe;

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

}
