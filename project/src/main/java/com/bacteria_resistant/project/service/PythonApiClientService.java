package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.dto.response.PredictionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PythonApiClientService {

    @Value("${ai.engine.url}")
    private String pythonApiUrl;

    public PredictionResponse enviarParaIa(String fileKey, String filename) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Key", "troque-esta-chave-em-producao");


        Map<String, String> requestBody = Map.of(
                "fileKey", fileKey,
                "fileName", filename
        );

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<PredictionResponse> response = restTemplate.postForEntity(
                pythonApiUrl,
                requestEntity,
                PredictionResponse.class
        );

        return response.getBody();
    }
}