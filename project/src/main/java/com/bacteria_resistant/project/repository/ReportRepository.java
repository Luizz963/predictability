package com.bacteria_resistant.project.repository;


import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Page<Report> findByLaboratory(Laboratory laboratory, Pageable pageable);
}
