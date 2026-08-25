package com.bacteria_resistant.project.service;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {

    private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

    public void uploadFile(String fileKey, InputStream inputStream, long contentLength, String contentType) {
        try {
            Files.copy(inputStream, tempDir.resolve(fileKey), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar arquivo localmente no mock do S3", e);
        }
    }

    public InputStream downloadFile(String fileKey) {
        try {
            return Files.newInputStream(tempDir.resolve(fileKey));
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler arquivo localmente no mock do S3", e);
        }
    }
}