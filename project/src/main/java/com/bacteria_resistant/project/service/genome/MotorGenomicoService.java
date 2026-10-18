package com.bacteria_resistant.project.service.genome;

import com.bacteria_resistant.project.dto.response.PredictionResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class MotorGenomicoService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String internalKey;

    public MotorGenomicoService(
            RestTemplate restTemplate,
            @Value("${python.api.url:http://localhost:5000/predict}") String apiUrl,
            @Value("${python.api.internal-key}") String internalKey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.internalKey = internalKey;
    }

    public PredictionResponseDTO analyzeGenome(String fileKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("X-Internal-Key", internalKey);

        Map<String, String> body = new HashMap<>();
        body.put("fileKey", fileKey);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            return restTemplate.postForObject(apiUrl, request, PredictionResponseDTO.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Falha de comunicação com a API Python de Infeência: " + e.getMessage(), e);
        }
    }
}