package com.example.Menu_Analyzer.dto;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuResponse {

    private Long id;
    private String imagePath;
    private String ocrText;
    private OffsetDateTime createdAt;
    private List<DishResponse> dishes;
}

