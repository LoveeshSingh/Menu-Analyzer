package com.example.Menu_Analyzer.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuUploadRequest {
	private MultipartFile file;
	private String metadata; // Example metadata field
}
