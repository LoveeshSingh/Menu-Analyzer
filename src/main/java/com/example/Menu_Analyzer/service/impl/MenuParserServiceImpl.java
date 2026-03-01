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

    // Matches: "Dish Name" (no newlines allowed) followed by an optional $ and
    // "Price"
    // ([^\\r\\n\\t0-9][^\\r\\n\\t]*?) = Dish Name (Starts with
    // not-newline/tab/number, followed by not-newline/tab)
    // [\\t\\s]* = Any spaces or tabs between name and price
    // \\$? = Optional Dollar Sign
    // (\\d+(?:\\.\\d{2})?) = Price
    private static final Pattern DISH_PRICE_PATTERN = Pattern
            .compile("([^\\r\\n\\t0-9][^\\r\\n\\t]*?)[\\t\\s]*\\$?(\\d+\\.\\d{2}|\\d+)");

    @Override
    public List<Dish> parseAndPersistDishes(Menu menu) {
        String text = menu.getOcrText();
        if (!StringUtils.hasText(text)) {
            return List.of();
        }

        // We keep the original text, newlines intact, so we don't accidentally merge
        // lines
        String cleanText = text;

        List<Dish> dishes = new ArrayList<>();
        int position = 0;

        // 2. Use a Matcher to find every occurrence of "Dish Name + Price"
        Matcher matcher = DISH_PRICE_PATTERN.matcher(cleanText);

        while (matcher.find()) {
            String name = matcher.group(1).trim();
            BigDecimal price = new BigDecimal(matcher.group(2).trim());

            // 3. Skip invalid names or massive numbers (sanity check)
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
                    .category(null) // Can be updated later with enrichment
                    .positionIndex(position++)
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
