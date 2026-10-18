package com.bacteria_resistant.project.service.surveillance;

import com.bacteria_resistant.project.dto.HospitalUnitDTO;
import com.bacteria_resistant.project.exception.ResourceNotFoundException;
import com.bacteria_resistant.project.model.HospitalUnit;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.settings.SettingsChangeLog;
import com.bacteria_resistant.project.repository.HospitalUnitRepository;
import com.bacteria_resistant.project.repository.settings.SettingsChangeLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HospitalUnitService {

    private static final int BED_COUNT_DEFAULT = 10;

    private final HospitalUnitRepository repository;
    private final SettingsChangeLogRepository logRepository;

    public HospitalUnitService(HospitalUnitRepository repository,
                               SettingsChangeLogRepository logRepository) {
        this.repository = repository;
        this.logRepository = logRepository;
    }

    @Transactional(readOnly = true)
    public List<HospitalUnitDTO> listActive(Laboratory lab) {
        return repository.findByLaboratoryAndActiveTrueOrderByName(lab).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public HospitalUnitDTO create(Laboratory lab, HospitalUnitDTO dto) {
        HospitalUnit u = new HospitalUnit();
        u.setLaboratory(lab);
        u.setName(normalizeName(dto.name()));
        u.setBedCount(dto.bedCount() != null ? dto.bedCount() : BED_COUNT_DEFAULT);
        u.setActive(true);
        HospitalUnit saved = repository.save(u);

        audit(lab, "Unidade \"" + saved.getName() + "\" adicionada ao monitoramento");
        return toDTO(saved);
    }

    @Transactional
    public void deactivate(Laboratory lab, Long id) {
        HospitalUnit u = findOrThrow(lab, id);
        u.setActive(false);
        repository.save(u);

        audit(lab, "Unidade \"" + u.getName() + "\" removida do monitoramento");
    }

    @Transactional
    public HospitalUnitDTO update(Laboratory lab, Long id, HospitalUnitDTO dto) {
        HospitalUnit u = findOrThrow(lab, id);

        List<String> changes = new ArrayList<>();

        if (dto.name() != null && !dto.name().trim().equals(u.getName())) {
            changes.add("Unidade \"" + u.getName() + "\" renomeada para \"" + dto.name().trim() + "\"");
            u.setName(normalizeName(dto.name()));
        }
        if (dto.bedCount() != null && !dto.bedCount().equals(u.getBedCount())) {
            changes.add("Leitos da unidade \"" + u.getName() + "\": " + u.getBedCount() + " → " + dto.bedCount());
            u.setBedCount(dto.bedCount());
        }

        HospitalUnit saved = repository.save(u);

        if (!changes.isEmpty()) {
            audit(lab, String.join("; ", changes));
        }
        return toDTO(saved);
    }

    /* ---------- privados ---------- */

    private HospitalUnit findOrThrow(Laboratory lab, Long id) {
        return repository.findByIdAndLaboratory(id, lab)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada."));
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O nome da unidade é obrigatório.");
        }
        return name.trim();
    }

    private void audit(Laboratory lab, String description) {
        SettingsChangeLog log = new SettingsChangeLog();
        log.setLaboratory(lab);
        log.setChangedAt(LocalDateTime.now());
        log.setChangedBy(lab.getName());
        log.setDescription(description);
        logRepository.save(log);
    }

    private HospitalUnitDTO toDTO(HospitalUnit u) {
        return new HospitalUnitDTO(u.getId(), u.getName(), u.getBedCount(), u.getActive());
    }
}