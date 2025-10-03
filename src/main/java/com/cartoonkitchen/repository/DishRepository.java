package com.cartoonkitchen.repository;

import com.cartoonkitchen.entity.Dish;
import com.cartoonkitchen.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long> {

    @Query("select d from Dish d where d.category.name = :categoryName")
    List<Dish> findAllByCategoryName(@Param("categoryName") String categoryName);

    @Query("select d from Dish d where lower(d.name) like lower(concat('%', :part, '%'))")
    List<Dish> searchByNameLike(@Param("part") String part);

    @Query("select distinct d from Dish d join d.ingredients i where i.id in :ids group by d having count(distinct i.id) = :cnt")
    List<Dish> findByAllIngredientIds(@Param("ids") List<Long> ids, @Param("cnt") long cnt);

    @Query("select d from Dish d left join d.steps s group by d order by count(s) desc")
    Page<Dish> findTopByStepsCount(Pageable pageable);

    Page<Dish> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Dish> findAllByAuthor(User author);

}
