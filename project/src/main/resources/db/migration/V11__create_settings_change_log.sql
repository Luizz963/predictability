CREATE TABLE tb_settings_change_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    laboratory_id BIGINT NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(255),
    description VARCHAR(500),
    CONSTRAINT fk_log_laboratory
        FOREIGN KEY (laboratory_id) REFERENCES tb_laboratories(id)
);

CREATE INDEX idx_log_lab_date ON tb_settings_change_log(laboratory_id, changed_at);