package com.example.Menu_Analyzer.mapper;

import com.example.Menu_Analyzer.dto.DishResponse;
import com.example.Menu_Analyzer.dto.MenuResponse;
import com.example.Menu_Analyzer.dto.NutritionDto;
import com.example.Menu_Analyzer.entity.Dish;
import com.example.Menu_Analyzer.entity.Menu;
import com.example.Menu_Analyzer.service.FoodDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuMapper {

	private final FoodDataService foodDataService;

	public MenuResponse toMenuResponse(Menu menu, List<Dish> dishes) {
		List<DishResponse> dishResponses = dishes.stream().map(this::toDishResponse).toList();
		return MenuResponse.builder()
				.id(menu.getId())
				.imagePath(menu.getImagePath())
				.ocrText(menu.getOcrText())
				.createdAt(menu.getCreatedAt())
				.dishes(dishResponses)
				.build();
	}

	public DishResponse toDishResponse(Dish dish) {
		NutritionDto nutritionDto = foodDataService.getNutritionForDish(dish);

		return DishResponse.builder()
				.id(dish.getId())
				.name(dish.getName())
				.description(dish.getDescription())
				.price(dish.getPrice())
				.imageUrl(dish.getImageUrl())
				.dietType(dish.getDietType())
				.recipeText(dish.getRecipeText())
				.nutrition(nutritionDto)
				.build();
	}
}
