package com.bacteria_resistant.project.repository.settings;

import com.bacteria_resistant.project.model.settings.InstitutionSettings;
import com.bacteria_resistant.project.model.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstitutionSettingsRepository extends JpaRepository<InstitutionSettings, Long> {
    Optional<InstitutionSettings> findByLaboratory(Laboratory laboratory);
}