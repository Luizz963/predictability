CREATE TABLE tb_hospital_units (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    laboratory_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    bed_count INT NOT NULL DEFAULT 10,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_units_laboratory FOREIGN KEY (laboratory_id) REFERENCES tb_laboratories(id),
    CONSTRAINT uq_unit_lab_name UNIQUE (laboratory_id, name)
);