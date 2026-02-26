package com.example.Menu_Analyzer.service;

import org.springframework.web.multipart.MultipartFile;

public interface OcrService {

    String extractText(MultipartFile imageFile);
}

