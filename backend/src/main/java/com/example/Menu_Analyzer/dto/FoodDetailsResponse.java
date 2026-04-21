package com.example.Menu_Analyzer.dto;

import com.example.Menu_Analyzer.entity.DietType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDetailsResponse {
	private String name;
	private List<String> aliases;
	private String imageUrl;
	private DietType dietType;
	private String recipeText;
	private NutritionDto nutrition;
}
