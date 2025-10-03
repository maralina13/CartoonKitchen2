package com.cartoonkitchen.controller.api;

import com.cartoonkitchen.dto.DishApiResponseDTO;
import com.cartoonkitchen.entity.Dish;
import com.cartoonkitchen.entity.User;
import com.cartoonkitchen.repository.DishRepository;
import com.cartoonkitchen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishRestController {

    private final DishRepository dishRepository;
    private final UserRepository userRepository;

    @GetMapping
    public List<DishApiResponseDTO> getAll() {
        log.info("Получен список всех блюд (GET /api/dishes)");
        return dishRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishApiResponseDTO> getById(@PathVariable Long id) {
        log.info("Запрос блюда по id = {}", id);
        return dishRepository.findById(id)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Блюдо с id = {} не найдено", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<Dish> createDish(@RequestBody DishApiResponseDTO dto) {
        log.info("Создание нового блюда: {}", dto.getName());
        Dish dish = new Dish();
        dish.setName(dto.getName());
        dish.setRecipe(dto.getRecipe());

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                dish.setAuthor(user);
                log.info("Блюдо будет связано с пользователем: {}", username);
            }
        }

        dishRepository.save(dish);
        log.info("Блюдо '{}' успешно сохранено (id = {})", dish.getName(), dish.getId());
        return ResponseEntity.ok(dish);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dish> updateDish(@PathVariable Long id, @RequestBody DishApiResponseDTO dto) {
        log.info("Обновление блюда id = {}", id);
        return dishRepository.findById(id).map(dish -> {
            dish.setName(dto.getName());
            dish.setRecipe(dto.getRecipe());
            dishRepository.save(dish);
            log.info("Блюдо id = {} успешно обновлено", id);
            return ResponseEntity.ok(dish);
        }).orElseGet(() -> {
            log.warn("Блюдо для обновления не найдено (id = {})", id);
            return ResponseEntity.notFound().build();
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        if (dishRepository.existsById(id)) {
            dishRepository.deleteById(id);
            log.info("Блюдо удалено (id = {})", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("Попытка удалить несуществующее блюдо (id = {})", id);
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public List<DishApiResponseDTO> search(@RequestParam("q") String q) {
        log.info("Поиск блюд по подстроке: {}", q);
        return dishRepository.searchByNameLike(q).stream()
                .map(this::mapToDto)
                .toList();
    }

    private DishApiResponseDTO mapToDto(Dish dish) {
        DishApiResponseDTO dto = new DishApiResponseDTO();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setRecipe(dish.getRecipe());
        dto.setCategory(dish.getCategory() != null ? dish.getCategory().getName() : null);
        return dto;
    }
}
