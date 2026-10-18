CREATE TABLE tb_institution_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    laboratory_id BIGINT NOT NULL UNIQUE,
    window_days INT DEFAULT 7,
    min_isolates INT DEFAULT 3,
    prevalence_threshold INT DEFAULT 50,
    min_confidence INT DEFAULT 85,
    technical_responsible VARCHAR(255),
    ccih_email VARCHAR(255),
    CONSTRAINT fk_settings_laboratory
        FOREIGN KEY (laboratory_id) REFERENCES tb_laboratories(id)
);