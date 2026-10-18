package com.bacteria_resistant.project.model;

import com.bacteria_resistant.project.model.enums.ReportStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_reports")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = Long.class)})
@Filter(name = "tenantFilter", condition = "laboratory_id = :tenantId")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private Integer resistanceSignal;
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

    @Column(name = "organism")
    private String organism;

    @Column(name = "phenotype_confirmed")
    private Boolean phenotypeConfirmed = false;

    @Column(name = "actual_resistance")
    private Integer actualResistance;

    @Column(name = "confidence_level")
    private String confidenceLevel;

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

    public Integer getResistanceSignal() {
        return resistanceSignal;
    }

    public void setResistanceSignal(Integer resistanceSignal) {
        this.resistanceSignal = resistanceSignal;
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

    public Integer getActualResistance() {
        return actualResistance;
    }

    public void setActualResistance(Integer actualResistance) {
        this.actualResistance = actualResistance;
    }

    public Boolean getPhenotypeConfirmed() {
        return phenotypeConfirmed;
    }

    public void setPhenotypeConfirmed(Boolean phenotypeConfirmed) {
        this.phenotypeConfirmed = phenotypeConfirmed;
    }

    public String getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(String confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public String getOrganism() { return organism; }


    public void setOrganism(String organism) { this.organism = organism; }
}

