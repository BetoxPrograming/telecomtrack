USE telecomtrack;

ALTER TABLE material
    ADD COLUMN valor_unitario DECIMAL(10,2) NOT NULL DEFAULT 0.00;
