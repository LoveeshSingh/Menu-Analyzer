package com.example.Menu_Analyzer.service.impl;

import com.example.Menu_Analyzer.entity.DietType;
import com.example.Menu_Analyzer.entity.Dish;
import com.example.Menu_Analyzer.entity.Menu;
import com.example.Menu_Analyzer.repository.DishRepository;
import com.example.Menu_Analyzer.service.MenuParserService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MenuParserServiceImpl implements MenuParserService {

    private final DishRepository dishRepository;

    // Regex matches: "Dish Name" followed by optional $ and "Price"
    private static final Pattern DISH_PRICE_PATTERN = Pattern
            .compile("([a-zA-Z &'()\\-.,]+?)[ \\t]+(?:Rs\\.?|\\$|₹)?[ \\t]*([0-9]+(?:\\.[0-9]{1,2})?)");

    @Override
    public List<Dish> parseAndPersistDishes(Menu menu) {
        String text = menu.getOcrText();
        if (!StringUtils.hasText(text)) {
            return List.of();
        }

        // Keep original text formatting to avoid merging lines
        String cleanText = text
                .replace("\\t", "\t")
                .replace("\\n", "\n")
                .replace("\\r", "\r");

        List<Dish> dishes = new ArrayList<>();

        // Use a Matcher to find "Dish Name + Price" occurrences
        Matcher matcher = DISH_PRICE_PATTERN.matcher(cleanText);

        while (matcher.find()) {
            String name = matcher.group(1).trim();
            BigDecimal price = new BigDecimal(matcher.group(2).trim());

            // Skip invalid names or massive numbers
            if (name.isEmpty() || name.length() < 2 ||
                    name.equalsIgnoreCase("FOOD NAME") ||
                    name.equalsIgnoreCase("Veg") ||
                    name.equalsIgnoreCase("Non Veg") ||
                    name.equalsIgnoreCase("OFF")) {
                continue;
            }

            if (price.compareTo(new BigDecimal("1000")) > 0) {
                continue;
            }

            Dish dish = Dish.builder()
                    .menu(menu)
                    .name(name)
                    .price(price)
                    .description(null) // No longer tracking descriptions since we merged all lines
                    .imageUrl(null)
                    .dietType(DietType.UNKNOWN)
                    .recipeText(null)
                    .build();

            dishes.add(dish);
        }

        return dishRepository.saveAll(dishes);
    }
}
