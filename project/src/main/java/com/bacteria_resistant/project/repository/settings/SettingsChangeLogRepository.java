package com.bacteria_resistant.project.repository.settings;

import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.settings.SettingsChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingsChangeLogRepository extends JpaRepository<SettingsChangeLog, Long> {
    List<SettingsChangeLog> findTop10ByLaboratoryOrderByChangedAtDesc(Laboratory laboratory);
}