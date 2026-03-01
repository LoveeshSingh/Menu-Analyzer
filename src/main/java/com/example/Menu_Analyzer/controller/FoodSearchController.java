package com.example.Menu_Analyzer.controller;

import com.example.Menu_Analyzer.dto.FoodDetailsResponse;
import com.example.Menu_Analyzer.service.NutritionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
public class FoodSearchController {

	private final NutritionService nutritionService;

	@GetMapping("/search")
	public FoodDetailsResponse searchFood(@RequestParam("query") String query) {
		return nutritionService.searchFood(query);
	}
}
