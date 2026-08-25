CREATE TABLE tb_surveillance_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hospital_name VARCHAR(100) NOT NULL,
    hospital_unit VARCHAR(50) NOT NULL,
    window_start DATETIME(6) NOT NULL,
    window_end DATETIME(6) NOT NULL,
    sample_count INT NOT NULL,
    high_confidence_count INT NOT NULL,
    prevalence_rate DOUBLE NOT NULL,
    alert_triggered BOOLEAN NOT NULL,
    generated_at DATETIME(6) NOT NULL,
    laboratory_id BIGINT NOT NULL,
    CONSTRAINT fk_alerts_laboratory FOREIGN KEY (laboratory_id) REFERENCES tb_laboratories(id)
);