package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.dto.response.PredictionResponse;
import org.springframework.beans.factory.annotation.Value;
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

    // Extrai a rota dinamicamente da infraestrutura (http://python-ai:8023/predict)
    @Value("${ai.engine.url}")
    private String pythonApiUrl;

    private final StorageService storageService;

    public PythonApiClientService(StorageService storageService) {
        this.storageService = storageService;
    }

    public PredictionResponse enviarParaIa(String fileKey, String filename) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        headers.set("X-Internal-Key", "troque-esta-chave-em-producao");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        InputStream s3InputStream = storageService.downloadFile(fileKey);

        body.add("file", new InputStreamResource(s3InputStream) {
            @Override
            public String getFilename() {
                return filename;
            }

            @Override
            public long contentLength() {
                return -1;
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Dispara o POST usando a variável injetada
        ResponseEntity<PredictionResponse> response = restTemplate.postForEntity(
                pythonApiUrl,
                requestEntity,
                PredictionResponse.class
        );

        return response.getBody();
    }
}