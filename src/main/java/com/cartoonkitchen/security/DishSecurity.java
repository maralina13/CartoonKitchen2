// DishSecurity.java
package com.cartoonkitchen.security;

import com.cartoonkitchen.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("dishSecurity")
@RequiredArgsConstructor
public class DishSecurity {

    private final DishRepository dishRepository;

    public boolean isOwner(Long dishId, Authentication auth) {
        if (auth == null || auth.getName() == null) return false;
        return dishRepository.findById(dishId)
                .map(d -> d.getAuthor() != null && auth.getName().equals(d.getAuthor().getUsername()))
                .orElse(false);
    }
}
