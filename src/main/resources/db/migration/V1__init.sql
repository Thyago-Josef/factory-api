-- V1__create_initial_schema.sql
-- CORRIGIDO para funcionar com Panache Repository + Quarkus

-- =========================
-- SEQUÊNCIAS (em vez de IDENTITY)
-- =========================
CREATE SEQUENCE product_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE raw_material_seq START WITH 1 INCREMENT BY 1;

-- =========================
-- RAW MATERIAL
-- =========================
CREATE TABLE raw_material (
                              id                  NUMBER(19)      NOT NULL,  -- ✅ SEM IDENTITY
                              code                VARCHAR2(50)    NOT NULL,
                              name                VARCHAR2(200)   NOT NULL,  -- ✅ 200 chars (boas práticas)
                              stock_quantity      NUMBER(15,3)    DEFAULT 0 NOT NULL,
                              created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,  -- ✅ Auditoria
                              updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,  -- ✅ Auditoria

                              CONSTRAINT pk_raw_material PRIMARY KEY (id),
                              CONSTRAINT uk_raw_material_code UNIQUE (code),
                              CONSTRAINT ck_raw_material_stock CHECK (stock_quantity >= 0)
);

-- Índices para RAW_MATERIAL
--CREATE INDEX idx_raw_material_code ON raw_material(code);
CREATE INDEX idx_raw_material_name ON raw_material(name);

-- Trigger para updated_at
CREATE OR REPLACE TRIGGER trg_raw_material_updated_at
BEFORE UPDATE ON raw_material
                  FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- =========================
-- PRODUCT
-- =========================
CREATE TABLE product (
                         id                  NUMBER(19)      NOT NULL,  -- ✅ SEM IDENTITY
                         code                VARCHAR2(50)    NOT NULL,
                         name                VARCHAR2(200)   NOT NULL,  -- ✅ 200 chars (boas práticas)
                         price               NUMBER(15,2)    NOT NULL,
                         created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,  -- ✅ Auditoria
                         updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,  -- ✅ Auditoria

                         CONSTRAINT pk_product PRIMARY KEY (id),
                         CONSTRAINT uk_product_code UNIQUE (code),
                         CONSTRAINT ck_product_price CHECK (price >= 0)
);

-- Índices para PRODUCT
-- CREATE INDEX idx_product_code ON product(code);
CREATE INDEX idx_product_name ON product(name);

-- Trigger para updated_at
CREATE OR REPLACE TRIGGER trg_product_updated_at
BEFORE UPDATE ON product
                  FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- =========================
-- PRODUCT_RAW_MATERIAL (Junction Table)
-- ✅ CHAVE COMPOSTA (sem id artificial)
-- =========================
CREATE TABLE product_raw_material (
                                      product_id          NUMBER(19)      NOT NULL,
                                      raw_material_id     NUMBER(19)      NOT NULL,
                                      quantity_required   NUMBER(15,3)    NOT NULL,  -- ✅ Renomeado para consistência
                                      created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,  -- ✅ Auditoria
                                      updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,  -- ✅ Auditoria

                                      CONSTRAINT pk_product_raw_material PRIMARY KEY (product_id, raw_material_id),  -- ✅ Chave composta
                                      CONSTRAINT fk_prm_product FOREIGN KEY (product_id)
                                          REFERENCES product(id) ON DELETE CASCADE,
                                      CONSTRAINT fk_prm_raw_material FOREIGN KEY (raw_material_id)
                                          REFERENCES raw_material(id) ON DELETE CASCADE,
                                      CONSTRAINT ck_prm_quantity CHECK (quantity_required > 0)
);

-- Índices para PRODUCT_RAW_MATERIAL (Performance)
CREATE INDEX idx_prm_product_id ON product_raw_material(product_id);
CREATE INDEX idx_prm_raw_material_id ON product_raw_material(raw_material_id);

-- Trigger para updated_at
CREATE OR REPLACE TRIGGER trg_prm_updated_at
BEFORE UPDATE ON product_raw_material
                  FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- =========================
-- COMENTÁRIOS (Documentação)
-- =========================
COMMENT ON TABLE product IS 'Stores finished products that can be manufactured';
COMMENT ON COLUMN product.id IS 'Primary key, managed by product_seq';
COMMENT ON COLUMN product.code IS 'Unique product code (e.g., PROD-001)';
COMMENT ON COLUMN product.name IS 'Product name';
COMMENT ON COLUMN product.price IS 'Product sale price';

COMMENT ON TABLE raw_material IS 'Stores raw materials used in production';
COMMENT ON COLUMN raw_material.id IS 'Primary key, managed by raw_material_seq';
COMMENT ON COLUMN raw_material.code IS 'Unique raw material code (e.g., RAW-001)';
COMMENT ON COLUMN raw_material.name IS 'Raw material name';
COMMENT ON COLUMN raw_material.stock_quantity IS 'Current quantity available in stock';

COMMENT ON TABLE product_raw_material IS 'Many-to-many relationship between products and raw materials';
COMMENT ON COLUMN product_raw_material.product_id IS 'Foreign key to product table';
COMMENT ON COLUMN product_raw_material.raw_material_id IS 'Foreign key to raw_material table';
COMMENT ON COLUMN product_raw_material.quantity_required IS 'Quantity of raw material needed to produce one unit of product';