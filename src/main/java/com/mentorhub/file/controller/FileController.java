package com.mentorhub.file.controller;

import com.mentorhub.common.dto.ApiResponse;
import com.mentorhub.file.entity.LearningResource;
import com.mentorhub.file.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/resources", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<LearningResource> upload(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam String resourceType,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String externalLink) throws IOException {
        return ApiResponse.ok(fileStorageService.upload(title, description, resourceType, file, externalLink));
    }

    @GetMapping("/resources/mentor/{mentorId}")
    public ApiResponse<List<LearningResource>> list(@PathVariable Long mentorId) {
        return ApiResponse.ok(fileStorageService.byMentor(mentorId));
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename) throws IOException {
        byte[] data = fileStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
