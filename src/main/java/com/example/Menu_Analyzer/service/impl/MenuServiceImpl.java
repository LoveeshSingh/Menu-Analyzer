package com.example.Menu_Analyzer.service.impl;

import com.example.Menu_Analyzer.dto.DishResponse;
import com.example.Menu_Analyzer.dto.MenuResponse;
import com.example.Menu_Analyzer.entity.Dish;
import com.example.Menu_Analyzer.entity.Menu;
import com.example.Menu_Analyzer.mapper.MenuMapper;
import com.example.Menu_Analyzer.repository.DishRepository;
import com.example.Menu_Analyzer.repository.MenuRepository;
import com.example.Menu_Analyzer.service.MenuParserService;
import com.example.Menu_Analyzer.service.MenuService;
import com.example.Menu_Analyzer.service.OcrService;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;
    private final OcrService ocrService;
    private final MenuParserService menuParserService;
    private final MenuMapper menuMapper;

    @Override
    public MenuResponse scanMenu(MultipartFile imageFile) {
        String imagePath = imageFile.getOriginalFilename();
        if (imagePath == null || imagePath.isBlank()) {
            imagePath = "uploaded-menu";
        }

        Menu menu = Menu.builder()
                .imagePath(imagePath)
                .createdAt(OffsetDateTime.now())
                .build();

        menu = menuRepository.save(menu);

        String ocrText = ocrService.extractText(imageFile);
        menu.setOcrText(ocrText);
        menu = menuRepository.save(menu);

        menuParserService.parseAndPersistDishes(menu);

        List<Dish> dishes = dishRepository.findByMenu(menu);

        return menuMapper.toMenuResponse(menu, dishes);
    }

    @Override
    public MenuResponse getMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + menuId));
        List<Dish> dishes = dishRepository.findByMenu(menu);
        return menuMapper.toMenuResponse(menu, dishes);
    }

    @Override
    public List<DishResponse> getDishes(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + menuId));
        List<Dish> dishes = dishRepository.findByMenu(menu);
        return dishes.stream().map(menuMapper::toDishResponse).toList();
    }

    @Override
    public DishResponse getDish(Long menuId, Long dishId) {
        Dish dish = dishRepository.findById(dishId)
                .filter(d -> d.getMenu().getId().equals(menuId))
                .orElseThrow(
                        () -> new IllegalArgumentException("Dish not found or does not belong to menu: " + dishId));
        return menuMapper.toDishResponse(dish);
    }
}
