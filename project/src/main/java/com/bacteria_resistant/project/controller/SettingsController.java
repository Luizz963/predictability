package com.bacteria_resistant.project.controller;

import com.bacteria_resistant.project.dto.settings.SettingsDTO;
import com.bacteria_resistant.project.dto.settings.SettingsViewDTO;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<SettingsViewDTO> get(@AuthenticationPrincipal Laboratory laboratory) {
        return ResponseEntity.ok(settingsService.getView(laboratory));
    }

    @PutMapping
    public ResponseEntity<SettingsViewDTO> update(@AuthenticationPrincipal Laboratory laboratory,
                                                  @RequestBody SettingsDTO dto) {
        return ResponseEntity.ok(settingsService.update(laboratory, dto));
    }
}