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

    private static final Pattern LINE_WITH_PRICE =
            Pattern.compile("^(.+?)\\s+(\\d+(?:\\.\\d{1,2})?)$");

    @Override
    public List<Dish> parseAndPersistDishes(Menu menu) {
        String text = menu.getOcrText();
        if (!StringUtils.hasText(text)) {
            return List.of();
        }

        List<Dish> dishes = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");
        int position = 0;

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            String name = line;
            BigDecimal price = null;

            Matcher matcher = LINE_WITH_PRICE.matcher(line);
            if (matcher.matches()) {
                name = matcher.group(1).trim();
                price = new BigDecimal(matcher.group(2));
            }

            Dish dish = Dish.builder()
                    .menu(menu)
                    .name(name)
                    .price(price)
                    .category(null)
                    .positionIndex(position++)
                    .description(null)
                    .imageUrl(null)
                    .dietType(DietType.UNKNOWN)
                    .recipeText(null)
                    .build();

            dishes.add(dish);
        }

        return dishRepository.saveAll(dishes);
    }
}

