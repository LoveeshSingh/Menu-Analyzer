package com.example.Menu_Analyzer.service;

import com.example.Menu_Analyzer.entity.Dish;
import com.example.Menu_Analyzer.entity.Menu;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MenuService {

    /**
     * Scan a menu image, persist Menu and Dish entities, and return the persisted Menu.
     */
    Menu scanMenu(MultipartFile imageFile);

    /**
     * Load a Menu entity by id (without mapping to DTOs).
     */
    Menu getMenu(Long menuId);

    /**
     * Load all Dish entities for a given menu id.
     */
    List<Dish> getDishes(Long menuId);
}

