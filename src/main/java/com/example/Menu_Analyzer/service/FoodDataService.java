package com.example.Menu_Analyzer.service;

import com.example.Menu_Analyzer.dto.FoodDetailsResponse;
import com.example.Menu_Analyzer.dto.NutritionDto;
import com.example.Menu_Analyzer.entity.Dish;

public interface FoodDataService {

    void enrichDish(Long dishId);

    FoodDetailsResponse searchFood(String query);

    NutritionDto getNutritionForDish(Dish dish);
}
