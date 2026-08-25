package com.bacteria_resistant.project.model;

import com.bacteria_resistant.project.model.enums.ReportStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private Integer ampicillinResistance;
    private Double probability;
    private LocalDateTime processingDate;

    @ManyToOne
    @JoinColumn(name = "laboratory_id", nullable = false)
    @JsonIgnore
    private Laboratory laboratory;

    @Column(nullable = false, unique = true)
    private String fileKey;

    @Column(name = "hospital_name")
    private String hospitalName;

    @Column(name = "hospital_unit")
    private String hospitalUnit;

    @Column(name = "detected_gene")
    private String detectedGene;

    public Report() {
    }


    public Report(String fileName, String fileKey, Laboratory laboratory) {
        this.fileName = fileName;
        this.fileKey = fileKey;
        this.laboratory = laboratory;
        this.status = ReportStatus.PENDING;
        this.processingDate = LocalDateTime.now();
    }

    public Laboratory getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(Laboratory laboratory) {
        this.laboratory = laboratory;
    }

    public LocalDateTime getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(LocalDateTime processingDate) {
        this.processingDate = processingDate;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public Integer getAmpicillinResistance() {
        return ampicillinResistance;
    }

    public void setAmpicillinResistance(Integer ampicillinResistance) {
        this.ampicillinResistance = ampicillinResistance;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalUnit() {
        return hospitalUnit;
    }

    public void setHospitalUnit(String hospitalUnit) {
        this.hospitalUnit = hospitalUnit;
    }

    public String getDetectedGene() {
        return detectedGene;
    }

    public void setDetectedGene(String detectedGene) {
        this.detectedGene = detectedGene;
    }
}

