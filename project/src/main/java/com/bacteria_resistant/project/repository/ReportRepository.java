package com.bacteria_resistant.project.repository;


import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {


    Page<Report> findByLaboratory(Laboratory laboratory, Pageable pageable);


    @Query("SELECT r FROM Report r WHERE r.laboratory = :laboratory " +
            "AND r.processingDate BETWEEN :startDate AND :endDate " +
            "AND r.resistanceSignal = 1 " +
            "AND r.probability >= :minProbability " +
            "ORDER BY r.processingDate DESC")
    Page<Report> findOutbreakPatterns(
            @Param("laboratory") Laboratory laboratory,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minProbability") Double minProbability,
            Pageable pageable);


    @Query("SELECT COUNT(r) FROM Report r WHERE r.laboratory = :lab AND r.processingDate >= :startDate")
    long countSamplesSince(@Param("lab") Laboratory lab, @Param("startDate") LocalDateTime startDate);


    @Query("SELECT COUNT(r) FROM Report r WHERE r.laboratory = :lab AND r.processingDate >= :startDate AND r.resistanceSignal = 1")
    long countResistantSamplesSince(@Param("lab") Laboratory lab, @Param("startDate") LocalDateTime startDate);


    @Query("SELECT COUNT(DISTINCT r.hospitalUnit) FROM Report r WHERE r.laboratory = :lab AND r.processingDate >= :startDate")
    long countMonitoredUnitsSince(@Param("lab") Laboratory lab, @Param("startDate") LocalDateTime startDate);


    @Query("SELECT r.hospitalUnit as unit, COUNT(r.id) as total, SUM(r.resistanceSignal) as resistantCount " +
            "FROM Report r WHERE r.laboratory = :lab AND r.processingDate >= :startDate " +
            "GROUP BY r.hospitalUnit " +
            "ORDER BY resistantCount DESC")
    List<Object[]> findClusterPotentials(@Param("lab") Laboratory lab, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.laboratory = :lab AND r.hospitalUnit = :unit AND r.processingDate BETWEEN :start AND :end")
    long countByLaboratoryAndHospitalUnitInWindow(@Param("lab") Laboratory lab, @Param("unit") String unit, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.laboratory = :lab AND r.hospitalUnit = :unit AND r.resistanceSignal = 1 AND r.processingDate BETWEEN :start AND :end")
    long countResistantByLaboratoryAndHospitalUnitInWindow(@Param("lab") Laboratory lab, @Param("unit") String unit, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.laboratory = :lab " +
            "AND r.hospitalUnit = :unit " +
            "AND r.processingDate BETWEEN :start AND :end " +
            "AND r.resistanceSignal = 1 " +
            "AND r.probability >= :minProb " +
            "AND r.detectedGene IS NOT NULL " +
            "AND r.detectedGene != 'não detectado'")
    long countHighConfidenceInWindow(
            @Param("lab") Laboratory lab,
            @Param("unit") String unit,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("minProb") Double minProb);

    @Query("SELECT r FROM Report r WHERE r.id = :id AND r.laboratory = :lab")
    java.util.Optional<Report> findByIdAndLaboratory(@Param("id") Long id, @Param("lab") Laboratory lab);
}
