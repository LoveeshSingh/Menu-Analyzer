package com.example.Menu_Analyzer.service.impl;

import com.example.Menu_Analyzer.entity.DietType;
import com.example.Menu_Analyzer.entity.Dish;
import com.example.Menu_Analyzer.entity.DishNutrition;
import com.example.Menu_Analyzer.repository.DishNutritionRepository;
import com.example.Menu_Analyzer.repository.DishRepository;
import com.example.Menu_Analyzer.service.FoodDataService;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class FoodDataServiceImpl implements FoodDataService {

    private final DishRepository dishRepository;
    private final DishNutritionRepository dishNutritionRepository;

    @Value("${spoonacular.api.key}")
    private String spoonacularApiKey;

    @Value("${spoonacular.api.url}")
    private String spoonacularApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void enrichDish(Long dishId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new IllegalArgumentException("Dish not found: " + dishId));

        String name = dish.getName() != null ? dish.getName() : "Dish";

        SpoonacularResult result = fetchFromSpoonacular(name);

        DishNutrition nutrition = dishNutritionRepository.findByDish(dish)
                .orElseGet(() -> DishNutrition.builder().dish(dish).build());

        nutrition.setCalories(result.calories);
        nutrition.setProteinGrams(result.protein);
        nutrition.setCarbsGrams(result.carbs);
        nutrition.setFatGrams(result.fat);
        nutrition.setDataSource(result.dataSource);
        nutrition.setLastUpdatedAt(OffsetDateTime.now());

        dish.setDietType(result.dietType);
        dish.setRecipeText(
                result.recipeText != null ? result.recipeText : "Recipe details not provided by external source.");
        dish.setImageUrl(result.imageUrl != null ? result.imageUrl : "/images/" + toSlug(name) + ".jpg");

        dishRepository.save(dish);
        dishNutritionRepository.save(nutrition);
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
        SpoonacularResult result = fetchFromSpoonacular(name);

        com.example.Menu_Analyzer.dto.NutritionDto nutritionDto = com.example.Menu_Analyzer.dto.NutritionDto.builder()
                .calories(result.calories)
                .proteinGrams(result.protein)
                .carbsGrams(result.carbs)
                .fatGrams(result.fat)
                .dataSource(result.dataSource)
                .build();

        return com.example.Menu_Analyzer.dto.FoodDetailsResponse.builder()
                .name(name)
                .aliases(List.of(name + " Search Result"))
                .imageUrl(result.imageUrl != null ? result.imageUrl : "/images/" + toSlug(name) + ".jpg")
                .dietType(result.dietType)
                .recipeText(result.recipeText != null ? result.recipeText
                        : "Recipe details not provided by external source.")
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

    private static class SpoonacularResult {
        double calories = 0.0;
        double protein = 0.0;
        double carbs = 0.0;
        double fat = 0.0;
        String dataSource = "spoonacular";
        DietType dietType = DietType.UNKNOWN;
        String imageUrl = null;
        String recipeText = null;
    }

    private SpoonacularResult fetchFromSpoonacular(String query) {
        SpoonacularResult result = new SpoonacularResult();
        try {
            String url = spoonacularApiUrl + "/recipes/complexSearch?query=" + query
                    + "&addRecipeNutrition=true&number=1&apiKey=" + spoonacularApiKey;
            String responseStr = restTemplate.getForObject(url, String.class);
            if (responseStr != null) {
                JsonNode root = objectMapper.readTree(responseStr);
                JsonNode results = root.path("results");
                if (results.isArray() && !results.isEmpty()) {
                    JsonNode firstResult = results.get(0);
                    result.imageUrl = firstResult.path("image").asText(null);

                    boolean vegetarian = firstResult.path("vegetarian").asBoolean(false);
                    boolean vegan = firstResult.path("vegan").asBoolean(false);
                    if (vegan || vegetarian) {
                        result.dietType = DietType.VEG;
                    } else if (firstResult.hasNonNull("vegetarian") && !vegetarian) {
                        result.dietType = DietType.NON_VEG;
                    }

                    JsonNode nutritionOpts = firstResult.path("nutrition");
                    if (!nutritionOpts.isMissingNode()) {
                        JsonNode nutrients = nutritionOpts.path("nutrients");
                        if (nutrients.isArray()) {
                            for (JsonNode nutrient : nutrients) {
                                String paramName = nutrient.path("name").asText("").toLowerCase();
                                double amount = nutrient.path("amount").asDouble(0.0);
                                if (paramName.contains("calories") || paramName.contains("energy")) {
                                    result.calories = amount;
                                } else if (paramName.contains("protein")) {
                                    result.protein = amount;
                                } else if (paramName.contains("carbohydrates") || paramName.equals("carbs")) {
                                    result.carbs = amount;
                                } else if (paramName.equals("fat")) {
                                    result.fat = amount;
                                }
                            }
                        }
                    }
                } else {
                    result.dataSource = "not-found";
                }
            } else {
                result.dataSource = "api-error";
            }
        } catch (Exception e) {
            result.dataSource = "fallback-error";
        }
        return result;
    }
}
