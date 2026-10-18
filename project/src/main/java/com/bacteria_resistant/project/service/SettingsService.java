package com.bacteria_resistant.project.service;

import com.bacteria_resistant.project.dto.settings.SettingsChangeLogDTO;
import com.bacteria_resistant.project.dto.settings.SettingsDTO;
import com.bacteria_resistant.project.dto.settings.SettingsViewDTO;
import com.bacteria_resistant.project.model.settings.InstitutionSettings;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.settings.SettingsChangeLog;
import com.bacteria_resistant.project.repository.settings.InstitutionSettingsRepository;
import com.bacteria_resistant.project.repository.LaboratoryRepository;
import com.bacteria_resistant.project.repository.settings.SettingsChangeLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SettingsService {

    private final InstitutionSettingsRepository settingsRepository;
    private final SettingsChangeLogRepository logRepository;
    private final LaboratoryRepository laboratoryRepository;

    public SettingsService(InstitutionSettingsRepository settingsRepository,
                           SettingsChangeLogRepository logRepository,
                           LaboratoryRepository laboratoryRepository) {
        this.settingsRepository = settingsRepository;
        this.logRepository = logRepository;
        this.laboratoryRepository = laboratoryRepository;
    }

    @Transactional
    public InstitutionSettings getOrCreateEntity(Laboratory lab) {
        return settingsRepository.findByLaboratory(lab).orElseGet(() -> {
            InstitutionSettings s = new InstitutionSettings();
            s.setLaboratory(lab);
            return settingsRepository.save(s);
        });
    }

    @Transactional
    public SettingsViewDTO getView(Laboratory lab) {
        InstitutionSettings s = getOrCreateEntity(lab);
        return new SettingsViewDTO(toDTO(s, lab), history(lab));
    }

    @Transactional
    public SettingsViewDTO update(Laboratory lab, SettingsDTO dto) {
        InstitutionSettings s = getOrCreateEntity(lab);
        List<String> changes = new ArrayList<>();

        diff(changes, "Janela móvel de surto", s.getWindowDays(), dto.windowDays(), " dias");
        diff(changes, "Mínimo de isolados por cluster", s.getMinIsolates(), dto.minIsolates(), " amostras");
        diff(changes, "Limiar de prevalência crítica", s.getPrevalenceThreshold(), dto.prevalenceThreshold(), "%");
        diff(changes, "Confiança mínima da análise", s.getMinConfidence(), dto.minConfidence(), "%");

        if (dto.windowDays() != null) s.setWindowDays(dto.windowDays());
        if (dto.minIsolates() != null) s.setMinIsolates(dto.minIsolates());
        if (dto.prevalenceThreshold() != null) s.setPrevalenceThreshold(dto.prevalenceThreshold());
        if (dto.minConfidence() != null) s.setMinConfidence(dto.minConfidence());

        if (dto.technicalResponsible() != null && !Objects.equals(dto.technicalResponsible(), s.getTechnicalResponsible())) {
            changes.add("Responsável técnico alterado para " + dto.technicalResponsible());
            s.setTechnicalResponsible(dto.technicalResponsible());
        }
        if (dto.ccihEmail() != null && !Objects.equals(dto.ccihEmail(), s.getCcihEmail())) {
            changes.add("E-mail da CCIH alterado para " + dto.ccihEmail());
            s.setCcihEmail(dto.ccihEmail());
        }
        if (dto.institutionName() != null && !Objects.equals(dto.institutionName(), lab.getName())) {
            changes.add("Nome da instituição alterado para " + dto.institutionName());
            lab.setName(dto.institutionName());
            laboratoryRepository.save(lab);
        }

        settingsRepository.save(s);

        if (!changes.isEmpty()) {
            SettingsChangeLog log = new SettingsChangeLog();
            log.setLaboratory(lab);
            log.setChangedAt(LocalDateTime.now());
            log.setChangedBy(s.getTechnicalResponsible() != null ? s.getTechnicalResponsible() : lab.getName());
            log.setDescription(String.join("; ", changes));
            logRepository.save(log);
        }

        return new SettingsViewDTO(toDTO(s, lab), history(lab));
    }

    private void diff(List<String> out, String label, Integer oldV, Integer newV, String suffix) {
        if (newV == null || newV.equals(oldV)) return;
        out.add(label + " " + oldV + suffix + " → " + newV + suffix);
    }

    private List<SettingsChangeLogDTO> history(Laboratory lab) {
        return logRepository.findTop10ByLaboratoryOrderByChangedAtDesc(lab).stream()
                .map(l -> new SettingsChangeLogDTO(l.getChangedAt(), l.getChangedBy(), l.getDescription()))
                .toList();
    }

    private SettingsDTO toDTO(InstitutionSettings s, Laboratory lab) {
        return new SettingsDTO(
                s.getWindowDays(),
                s.getMinIsolates(),
                s.getPrevalenceThreshold(),
                s.getMinConfidence(),
                lab.getName(),
                lab.getTaxId(),
                s.getTechnicalResponsible(),
                s.getCcihEmail()
        );
    }
}