package com.bacteria_resistant.project.repository;

import com.bacteria_resistant.project.model.HospitalUnit;
import com.bacteria_resistant.project.model.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HospitalUnitRepository extends JpaRepository<HospitalUnit, Long> {
    List<HospitalUnit> findByLaboratoryAndActiveTrueOrderByName(Laboratory laboratory);
    Optional<HospitalUnit> findByIdAndLaboratory(Long id, Laboratory laboratory);
}