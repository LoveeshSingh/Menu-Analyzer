package com.example.Menu_Analyzer.dto;

import com.example.Menu_Analyzer.entity.DietType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishResponse {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private DietType dietType;
    private String recipeText;
    private java.math.BigDecimal price;
    private NutritionDto nutrition;
}
