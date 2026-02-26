package com.example.Menu_Analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionDto {

    private Double calories;
    private Double proteinGrams;
    private Double carbsGrams;
    private Double fatGrams;
    private String dataSource;
}

