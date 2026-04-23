package com.example.Menu_Analyzer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.noContent().build();
    }
}
