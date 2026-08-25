package com.bacteria_resistant.project.repository;

import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.SurveillanceAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface SurveillanceAlertRepository extends JpaRepository<SurveillanceAlert, Long> {
    Page<SurveillanceAlert> findByLaboratoryAndGeneratedAtBetween(
            Laboratory laboratory, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}