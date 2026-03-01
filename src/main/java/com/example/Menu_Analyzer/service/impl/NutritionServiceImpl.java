package com.example.Menu_Analyzer.service.impl;

import com.example.Menu_Analyzer.entity.DietType;
import com.example.Menu_Analyzer.entity.Dish;
import com.example.Menu_Analyzer.entity.DishNutrition;
import com.example.Menu_Analyzer.entity.Menu;
import com.example.Menu_Analyzer.repository.DishNutritionRepository;
import com.example.Menu_Analyzer.repository.DishRepository;
import com.example.Menu_Analyzer.repository.MenuRepository;
import com.example.Menu_Analyzer.service.NutritionService;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class NutritionServiceImpl implements NutritionService {

    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;
    private final DishNutritionRepository dishNutritionRepository;

    @Override
    public void enrichMenuDishes(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + menuId));

        List<Dish> dishes = dishRepository.findByMenu(menu);
        for (Dish dish : dishes) {
            enrichDish(dish.getId());
        }
    }

    @Override
    public void enrichDish(Long dishId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new IllegalArgumentException("Dish not found: " + dishId));

        // Stub enrichment logic: in a real implementation, this would call an external
        // nutrition/recipe API. Here we just generate deterministic example data.
        String name = dish.getName() != null ? dish.getName() : "Dish";

        double baseCalories = 200.0 + name.length() * 5.0;
        double protein = baseCalories * 0.15 / 4.0; // rough grams from 15% calories
        double carbs = baseCalories * 0.55 / 4.0; // 55% calories
        double fat = baseCalories * 0.30 / 9.0; // 30% calories

        String lowerName = name.toLowerCase();
        DietType dietType = DietType.VEG;
        if (containsAny(lowerName, "chicken", "mutton", "fish", "egg", "prawn", "meat")) {
            dietType = DietType.NON_VEG;
        } else if (containsAny(lowerName, "paneer", "dal", "veg", "biryani")) {
            dietType = DietType.VEG;
        } else {
            dietType = DietType.UNKNOWN;
        }

        String recipeText = "Recipe for " + name + ":\n"
                + "1. Prepare the main ingredients.\n"
                + "2. Cook with appropriate spices and oil.\n"
                + "3. Adjust seasoning to taste and serve hot.";

        String imageUrl = "/images/" + toSlug(name) + ".jpg";

        DishNutrition nutrition = dishNutritionRepository.findByDish(dish)
                .orElseGet(() -> DishNutrition.builder().dish(dish).build());

        nutrition.setCalories(baseCalories);
        nutrition.setProteinGrams(protein);
        nutrition.setCarbsGrams(carbs);
        nutrition.setFatGrams(fat);
        nutrition.setDataSource("stub-example");
        nutrition.setLastUpdatedAt(OffsetDateTime.now());

        dish.setDietType(dietType);
        dish.setRecipeText(recipeText);
        dish.setImageUrl(imageUrl);

        dishRepository.save(dish);
        dishNutritionRepository.save(nutrition);
    }

    private boolean containsAny(String value, String... tokens) {
        for (String token : tokens) {
            if (value.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private String toSlug(String value) {
        if (!StringUtils.hasText(value)) {
            return "dish";
        }
        String slug = value.trim().toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
        return slug.isEmpty() ? "dish" : slug;
    }

    @Override
    public com.example.Menu_Analyzer.dto.FoodDetailsResponse searchFood(String query) {
        String name = query != null ? query : "Unknown Food";

        double baseCalories = 200.0 + name.length() * 5.0;
        double protein = baseCalories * 0.15 / 4.0;
        double carbs = baseCalories * 0.55 / 4.0;
        double fat = baseCalories * 0.30 / 9.0;

        String lowerName = name.toLowerCase();
        DietType dietType = DietType.VEG;
        if (containsAny(lowerName, "chicken", "mutton", "fish", "egg", "prawn", "meat")) {
            dietType = DietType.NON_VEG;
        } else if (containsAny(lowerName, "paneer", "dal", "veg", "biryani")) {
            dietType = DietType.VEG;
        } else {
            dietType = DietType.UNKNOWN;
        }

        String recipeText = "Recipe for " + name + ":\n"
                + "1. Prepare the main ingredients.\n"
                + "2. Cook with appropriate spices and oil.\n"
                + "3. Adjust seasoning to taste and serve hot.";

        String imageUrl = "/images/" + toSlug(name) + ".jpg";

        com.example.Menu_Analyzer.dto.NutritionDto nutritionDto = com.example.Menu_Analyzer.dto.NutritionDto.builder()
                .calories(baseCalories)
                .proteinGrams(protein)
                .carbsGrams(carbs)
                .fatGrams(fat)
                .dataSource("stub-search-example")
                .build();

        return com.example.Menu_Analyzer.dto.FoodDetailsResponse.builder()
                .name(name)
                .aliases(List.of(name + " Alias"))
                .imageUrl(imageUrl)
                .dietType(dietType)
                .recipeText(recipeText)
                .nutrition(nutritionDto)
                .build();
    }

    @Override
    public com.example.Menu_Analyzer.dto.NutritionDto getNutritionForDish(Dish dish) {
        DishNutrition nutrition = dishNutritionRepository.findByDish(dish).orElse(null);
        if (nutrition == null) {
            return null;
        }

        return com.example.Menu_Analyzer.dto.NutritionDto.builder()
                .calories(nutrition.getCalories())
                .proteinGrams(nutrition.getProteinGrams())
                .carbsGrams(nutrition.getCarbsGrams())
                .fatGrams(nutrition.getFatGrams())
                .dataSource(nutrition.getDataSource())
                .build();
    }
}
