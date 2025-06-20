package com.example.Jewelry.controller;

import com.example.Jewelry.service.ServiceImpl.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UploadController {
    private final CloudinaryService cloudinaryService;

    @PostMapping
    public ResponseEntity<List<String>> uploadImages(
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {
        List<String> urls = cloudinaryService.uploadFiles(files);
        return ResponseEntity.ok(urls);
    }
}
