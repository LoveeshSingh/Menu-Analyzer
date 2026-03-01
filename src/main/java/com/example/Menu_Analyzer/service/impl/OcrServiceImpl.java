package com.example.Menu_Analyzer.service.impl;

import com.example.Menu_Analyzer.service.OcrService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Service
public class OcrServiceImpl implements OcrService {

    private static final String OCR_SPACE_API_URL = "https://api.ocr.space/parse/image";

    @Value("${ocr.space.api.key}")
    private String ocrSpaceApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String extractText(MultipartFile imageFile) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("apikey", ocrSpaceApiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("language", "eng");
            body.add("isOverlayRequired", "false");
            body.add("isTable", "true");
            body.add("OCREngine", "2");
            body.add("file", new ByteArrayResource(imageFile.getBytes()) {
                @Override
                public String getFilename() {
                    return imageFile.getOriginalFilename() != null ? imageFile.getOriginalFilename() : "menu.png";
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(OCR_SPACE_API_URL, requestEntity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode parsedResults = root.path("ParsedResults");

                if (parsedResults.isArray() && !parsedResults.isEmpty()) {
                    return parsedResults.get(0).path("ParsedText").asText();
                } else {
                    System.err.println("OCR Error: " + root.path("ErrorMessage").asText());
                    return "Error parsing menu image.";
                }
            }
            return "Failed to communicate with OCR service.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading image file: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling OCR service: " + e.getMessage();
        }
    }
}
