package com.example.Menu_Analyzer.service.impl;

import com.example.Menu_Analyzer.entity.Dish;
import com.example.Menu_Analyzer.entity.Menu;
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

    @Override
    public Menu scanMenu(MultipartFile imageFile) {
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

        return menu;
    }

    @Override
    public Menu getMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + menuId));
    }

    @Override
    public List<Dish> getDishes(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + menuId));
        return dishRepository.findByMenu(menu);
    }
}

