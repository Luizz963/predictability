package com.bacteria_resistant.project.model.settings;

import com.bacteria_resistant.project.model.Laboratory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_institution_settings")
public class InstitutionSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "laboratory_id", nullable = false, unique = true)
    @JsonIgnore
    private Laboratory laboratory;

    @Column(name = "window_days")
    private Integer windowDays = 7;

    @Column(name = "min_isolates")
    private Integer minIsolates = 3;

    @Column(name = "prevalence_threshold")
    private Integer prevalenceThreshold = 50;

    @Column(name = "min_confidence")
    private Integer minConfidence = 85;

    @Column(name = "technical_responsible")
    private String technicalResponsible;

    @Column(name = "ccih_email")
    private String ccihEmail;

    public Long getId() { return id; }
    public Laboratory getLaboratory() { return laboratory; }
    public void setLaboratory(Laboratory laboratory) { this.laboratory = laboratory; }
    public Integer getWindowDays() { return windowDays; }
    public void setWindowDays(Integer windowDays) { this.windowDays = windowDays; }
    public Integer getMinIsolates() { return minIsolates; }
    public void setMinIsolates(Integer minIsolates) { this.minIsolates = minIsolates; }
    public Integer getPrevalenceThreshold() { return prevalenceThreshold; }
    public void setPrevalenceThreshold(Integer prevalenceThreshold) { this.prevalenceThreshold = prevalenceThreshold; }
    public Integer getMinConfidence() { return minConfidence; }
    public void setMinConfidence(Integer minConfidence) { this.minConfidence = minConfidence; }
    public String getTechnicalResponsible() { return technicalResponsible; }
    public void setTechnicalResponsible(String technicalResponsible) { this.technicalResponsible = technicalResponsible; }
    public String getCcihEmail() { return ccihEmail; }
    public void setCcihEmail(String ccihEmail) { this.ccihEmail = ccihEmail; }
}