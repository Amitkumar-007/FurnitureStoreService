package com.mart.furniture.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadImage(MultipartFile file) throws IOException {
        // Generate a unique file name
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Supabase Storage API endpoint
        String url = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucket, fileName);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(file.getContentType()));
        headers.set("Authorization", "Bearer " + supabaseKey);

        // Upload the file
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Return the public URL of the uploaded file
            return String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucket, fileName);
        } else {
            throw new IOException("Failed to upload file: " + response.getBody());
        }
    }
}