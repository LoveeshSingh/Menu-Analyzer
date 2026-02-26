package com.example.Menu_Analyzer.service.impl;

import com.example.Menu_Analyzer.service.OcrService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StubOcrService implements OcrService {

    @Override
    public String extractText(MultipartFile imageFile) {
        // Stub implementation: return hardcoded example text.
        // Later this can be replaced with a real OCR integration.
        return """
                Paneer Butter Masala 250
                Dal Tadka 200
                Veg Biryani 300
                """;
    }
}

