package com.example.Menu_Analyzer.controller;

import com.example.Menu_Analyzer.dto.FoodDetailsResponse;
import com.example.Menu_Analyzer.service.FoodDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
public class FoodSearchController {

	private final FoodDataService foodDataService;

	@GetMapping("/search")
	public FoodDetailsResponse searchFood(@RequestParam("query") String query) {
		return foodDataService.searchFood(query);
	}
}
