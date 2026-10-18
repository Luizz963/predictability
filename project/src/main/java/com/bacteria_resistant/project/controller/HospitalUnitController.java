package com.bacteria_resistant.project.controller;

import com.bacteria_resistant.project.dto.HospitalUnitDTO;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.service.surveillance.HospitalUnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/units")
public class HospitalUnitController {

    private final HospitalUnitService service;

    public HospitalUnitController(HospitalUnitService service) {
        this.service = service;
    }

    @GetMapping
    public List<HospitalUnitDTO> list(@AuthenticationPrincipal Laboratory lab) {
        return service.listActive(lab);
    }

    @PostMapping
    public ResponseEntity<HospitalUnitDTO> create(@AuthenticationPrincipal Laboratory lab,
                                                  @RequestBody HospitalUnitDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(lab, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@AuthenticationPrincipal Laboratory lab, @PathVariable Long id) {
        service.deactivate(lab, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<HospitalUnitDTO> update(@AuthenticationPrincipal Laboratory lab,
                                                  @PathVariable Long id,
                                                  @RequestBody HospitalUnitDTO dto) {
        return ResponseEntity.ok(service.update(lab, id, dto));
    }
}