package com.example.Menu_Analyzer.repository;

import com.example.Menu_Analyzer.entity.Dish;
import com.example.Menu_Analyzer.entity.Menu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<Dish, Long> {

    List<Dish> findByMenu(Menu menu);
}

