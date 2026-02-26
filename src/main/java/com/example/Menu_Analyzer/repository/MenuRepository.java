package com.example.Menu_Analyzer.repository;

import com.example.Menu_Analyzer.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}

