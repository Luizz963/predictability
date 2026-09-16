ALTER TABLE tb_reports ADD COLUMN phenotype_confirmed BOOLEAN DEFAULT FALSE;
ALTER TABLE tb_reports ADD COLUMN actual_resistance INT;