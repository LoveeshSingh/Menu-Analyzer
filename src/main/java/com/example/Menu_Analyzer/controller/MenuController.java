package com.example.Menu_Analyzer.controller;

import com.example.Menu_Analyzer.dto.DishResponse;
import com.example.Menu_Analyzer.dto.MenuResponse;
import com.example.Menu_Analyzer.entity.Dish;
import com.example.Menu_Analyzer.entity.Menu;
import com.example.Menu_Analyzer.mapper.MenuMapper;
import com.example.Menu_Analyzer.service.MenuService;
import com.example.Menu_Analyzer.service.NutritionService;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final NutritionService nutritionService;
    private final MenuMapper menuMapper;

    @PostMapping(value = "/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MenuResponse scanMenu(@RequestParam("image") @NotNull MultipartFile image) {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        Menu menu = menuService.scanMenu(image);
        List<Dish> dishes = menuService.getDishes(menu.getId());
        return menuMapper.toMenuResponse(menu, dishes);
    }

    @PostMapping("/{menuId}/enrich")
    public MenuResponse enrichMenu(@PathVariable("menuId") Long menuId) {
        nutritionService.enrichMenuDishes(menuId);

        Menu menu = menuService.getMenu(menuId);
        List<Dish> dishes = menuService.getDishes(menuId);
        return menuMapper.toMenuResponse(menu, dishes);
    }

    @GetMapping("/{menuId}")
    public MenuResponse getMenu(@PathVariable("menuId") Long menuId) {
        Menu menu = menuService.getMenu(menuId);
        List<Dish> dishes = menuService.getDishes(menuId);
        return menuMapper.toMenuResponse(menu, dishes);
    }

    @GetMapping("/{menuId}/dishes")
    public List<DishResponse> getDishes(@PathVariable("menuId") Long menuId) {
        List<Dish> dishes = menuService.getDishes(menuId);
        return dishes.stream().map(menuMapper::toDishResponse).toList();
    }

    @GetMapping("/{menuId}/dishes/{dishId}")
    public DishResponse getDish(@PathVariable("menuId") Long menuId, @PathVariable("dishId") Long dishId) {
        Dish dish = menuService.getDish(menuId, dishId);
        return menuMapper.toDishResponse(dish);
    }
}
