package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.dto.PredictionResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

@Service
public class PythonApiClientService {

    private final String PYTHON_API_URL = "http://localhost:8023/predict";
    private final StorageService storageService;

    public PythonApiClientService(StorageService storageService) {
        this.storageService = storageService;
    }

    public PredictionResponse enviarParaIa(String fileKey, String filename) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // Baixa o arquivo do S3 como Stream
        InputStream s3InputStream = storageService.downloadFile(fileKey);

        // InputStreamResource consome a stream sob demanda durante o envio HTTP
        body.add("file", new InputStreamResource(s3InputStream) {
            @Override
            public String getFilename() {
                return filename;
            }

            @Override
            public long contentLength() {
                return -1; // Força o envio em chunks (Chunked Transfer-Encoding)
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<PredictionResponse> response = restTemplate.postForEntity(
                PYTHON_API_URL,
                requestEntity,
                PredictionResponse.class
        );

        return response.getBody();
    }
}