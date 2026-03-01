package com.example.Menu_Analyzer.service;

import com.example.Menu_Analyzer.dto.DishResponse;
import com.example.Menu_Analyzer.dto.MenuResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MenuService {

    /**
     * Scan a menu image, persist entities, and return the DTO.
     */
    MenuResponse scanMenu(MultipartFile imageFile);

    /**
     * Load a Menu by id (mapped to DTO).
     */
    MenuResponse getMenu(Long menuId);

    /**
     * Load all Dishes for a given menu id.
     */
    List<DishResponse> getDishes(Long menuId);

    /**
     * Load a specific Dish by menu id and dish id.
     */
    DishResponse getDish(Long menuId, Long dishId);
}
