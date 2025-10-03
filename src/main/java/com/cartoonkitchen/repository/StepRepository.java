package com.cartoonkitchen.repository;

import com.cartoonkitchen.entity.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {

    @Transactional
    @Modifying
    @Query("delete from Step s where s.dish.id = :dishId")
    void deleteAllByDishId(@Param("dishId") Long dishId);
}
