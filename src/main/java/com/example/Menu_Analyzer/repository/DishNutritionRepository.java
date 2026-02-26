package com.example.Menu_Analyzer.repository;

import com.example.Menu_Analyzer.entity.Dish;
import com.example.Menu_Analyzer.entity.DishNutrition;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishNutritionRepository extends JpaRepository<DishNutrition, Long> {

    Optional<DishNutrition> findByDish(Dish dish);
}

