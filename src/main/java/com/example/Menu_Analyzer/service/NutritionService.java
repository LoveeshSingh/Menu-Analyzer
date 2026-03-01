package com.example.Menu_Analyzer.service;

public interface NutritionService {

    void enrichMenuDishes(Long menuId);

    void enrichDish(Long dishId);

    com.example.Menu_Analyzer.dto.FoodDetailsResponse searchFood(String query);

    com.example.Menu_Analyzer.dto.NutritionDto getNutritionForDish(com.example.Menu_Analyzer.entity.Dish dish);
}
