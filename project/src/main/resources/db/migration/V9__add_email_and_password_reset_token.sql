
ALTER TABLE tb_laboratories
ADD COLUMN email VARCHAR(255) NULL;


UPDATE tb_laboratories
SET email = CONCAT('lab_', id, '@hospital.com')
WHERE email IS NULL;


ALTER TABLE tb_laboratories
MODIFY COLUMN email VARCHAR(255) NOT NULL;

ALTER TABLE tb_laboratories
ADD CONSTRAINT uk_laboratories_email UNIQUE (email);


CREATE TABLE IF NOT EXISTS tb_password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    laboratory_id BIGINT NOT NULL,
    expiry_date DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_password_reset_laboratory
        FOREIGN KEY (laboratory_id) REFERENCES tb_laboratories(id)
        ON DELETE CASCADE
);