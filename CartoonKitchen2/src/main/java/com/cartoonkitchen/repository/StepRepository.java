package com.cartoonkitchen.repository;

import com.cartoonkitchen.entity.Step;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepRepository extends JpaRepository<Step, Long> {
}
