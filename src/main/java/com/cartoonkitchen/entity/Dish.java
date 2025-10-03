package com.cartoonkitchen.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private Category category;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @Column(name = "cartoon_image_url", nullable = true)
    private String cartoonImageUrl;

    @ManyToMany
    @JoinTable(
            name = "dish_ingredients",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "dish",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Step> steps = new ArrayList<>();


    @Column(columnDefinition = "TEXT")
    private String recipe;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "favorites")
    private Set<User> favoredBy = new HashSet<>();
}
