package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.dto.AiAnalysisRequest;
import com.bacteria_resistant.project.dto.response.AiAnalysisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class AiIntegrationService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai.engine.url}")
    private String aiEngineUrl;

    public AiAnalysisResponse getAnalysis(String fileKey) {
        AiAnalysisRequest request = new AiAnalysisRequest(fileKey);
        return restTemplate.postForObject(aiEngineUrl + "/predict", request, AiAnalysisResponse.class);
    }
}
