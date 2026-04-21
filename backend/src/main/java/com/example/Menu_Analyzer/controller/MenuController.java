package com.example.Menu_Analyzer.controller;

import com.example.Menu_Analyzer.dto.DishResponse;
import com.example.Menu_Analyzer.dto.MenuResponse;
import com.example.Menu_Analyzer.dto.MenuUploadRequest;
import com.example.Menu_Analyzer.service.FoodDataService;
import com.example.Menu_Analyzer.service.MenuService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final FoodDataService foodDataService;

    @PostMapping(value = "/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MenuResponse scanMenu(@Valid @ModelAttribute MenuUploadRequest request) {
        if (request.getFile().isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        return menuService.scanMenu(request.getFile());
    } 

    @PostMapping("/{menuId}/dishes/{dishId}/enrich")
    public DishResponse enrichDish(@PathVariable("menuId") Long menuId, @PathVariable("dishId") Long dishId) {
        foodDataService.enrichDish(dishId);
        return menuService.getDish(menuId, dishId);
    }

    @GetMapping("/{menuId}")
    public MenuResponse getMenu(@PathVariable("menuId") Long menuId) {
        return menuService.getMenu(menuId);
    }

    @GetMapping("/{menuId}/dishes")
    public List<DishResponse> getDishes(@PathVariable("menuId") Long menuId) {
        return menuService.getDishes(menuId);
    }

    @GetMapping("/{menuId}/dishes/{dishId}")
    public DishResponse getDish(@PathVariable("menuId") Long menuId, @PathVariable("dishId") Long dishId) {
        return menuService.getDish(menuId, dishId);
    }
}
