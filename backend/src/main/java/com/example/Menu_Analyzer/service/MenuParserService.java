package com.example.Menu_Analyzer.service;

import com.example.Menu_Analyzer.entity.Dish;
import com.example.Menu_Analyzer.entity.Menu;
import java.util.List;

public interface MenuParserService {

    List<Dish> parseAndPersistDishes(Menu menu);
}

