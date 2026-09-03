package com.bacteria_resistant.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_surveillance_alerts")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = Long.class)})
@Filter(name = "tenantFilter", condition = "laboratory_id = :tenantId")
public class SurveillanceAlert {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hospitalName;
    private String hospitalUnit;
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private Integer sampleCount;
    private Integer highConfidenceCount;
    private Double prevalenceRate;
    private Boolean alertTriggered;
    private LocalDateTime generatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laboratory_id", nullable = false)
    @JsonIgnore
    private Laboratory laboratory;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public String getHospitalUnit() { return hospitalUnit; }
    public void setHospitalUnit(String hospitalUnit) { this.hospitalUnit = hospitalUnit; }
    public LocalDateTime getWindowStart() { return windowStart; }
    public void setWindowStart(LocalDateTime windowStart) { this.windowStart = windowStart; }
    public LocalDateTime getWindowEnd() { return windowEnd; }
    public void setWindowEnd(LocalDateTime windowEnd) { this.windowEnd = windowEnd; }
    public Integer getSampleCount() { return sampleCount; }
    public void setSampleCount(Integer sampleCount) { this.sampleCount = sampleCount; }
    public Integer getHighConfidenceCount() { return highConfidenceCount; }
    public void setHighConfidenceCount(Integer highConfidenceCount) { this.highConfidenceCount = highConfidenceCount; }
    public Double getPrevalenceRate() { return prevalenceRate; }
    public void setPrevalenceRate(Double prevalenceRate) { this.prevalenceRate = prevalenceRate; }
    public Boolean getAlertTriggered() { return alertTriggered; }
    public void setAlertTriggered(Boolean alertTriggered) { this.alertTriggered = alertTriggered; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public Laboratory getLaboratory() { return laboratory; }
    public void setLaboratory(Laboratory laboratory) { this.laboratory = laboratory; }
}