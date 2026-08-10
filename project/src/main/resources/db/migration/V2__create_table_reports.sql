CREATE TABLE tb_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_key VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    ampicillin_resistance INT,
    probability DOUBLE,
    processing_date DATETIME(6),
    laboratory_id BIGINT NOT NULL,
    CONSTRAINT fk_reports_laboratory FOREIGN KEY (laboratory_id) REFERENCES tb_laboratories(id)
);