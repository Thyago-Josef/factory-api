-- V2__insert_sample_data.sql
-- CORRIGIDO para usar sequências e nomes corretos

-- =========================
-- RAW MATERIALS (Insumos Industriais)
-- =========================

INSERT INTO raw_material (id, code, name, stock_quantity)
VALUES (raw_material_seq.NEXTVAL, 'RM-STEEL', 'Chapa de Aço Carbono 3mm', 500.000);

INSERT INTO raw_material (id, code, name, stock_quantity)
VALUES (raw_material_seq.NEXTVAL, 'RM-ALUM', 'Perfil de Alumínio Anodizado', 400.000);

INSERT INTO raw_material (id, code, name, stock_quantity)
VALUES (raw_material_seq.NEXTVAL, 'RM-GLASS', 'Vidro Temperado 8mm', 200.000);

INSERT INTO raw_material (id, code, name, stock_quantity)
VALUES (raw_material_seq.NEXTVAL, 'RM-OAK', 'Painel de Madeira Carvalho', 300.000);

INSERT INTO raw_material (id, code, name, stock_quantity)
VALUES (raw_material_seq.NEXTVAL, 'RM-LEATH', 'Couro Sintético Automotivo', 150.000);

INSERT INTO raw_material (id, code, name, stock_quantity)
VALUES (raw_material_seq.NEXTVAL, 'RM-FOAM', 'Espuma D45 Soft', 320.000);

INSERT INTO raw_material (id, code, name, stock_quantity)
VALUES (raw_material_seq.NEXTVAL, 'RM-COPP', 'Fiação de Cobre 2.5mm', 350.000);

INSERT INTO raw_material (id, code, name, stock_quantity)
VALUES (raw_material_seq.NEXTVAL, 'RM-RESIN', 'Resina Epóxi Bi-Componente', 270.000);

-- =========================
-- PRODUCTS (Produtos Finais)
-- =========================

INSERT INTO product (id, code, name, price)
VALUES (product_seq.NEXTVAL, 'PR-OFF-DESK', 'Escrivaninha Industrial Executive', 1850.00);

INSERT INTO product (id, code, name, price)
VALUES (product_seq.NEXTVAL, 'PR-LUX-SOFA', 'Sofá Minimalista 3 Lugares', 4200.00);

INSERT INTO product (id, code, name, price)
VALUES (product_seq.NEXTVAL, 'PR-MTL-CAB', 'Armário Corta-Fogo de Aço', 2100.00);

INSERT INTO product (id, code, name, price)
VALUES (product_seq.NEXTVAL, 'PR-GLS-TAB', 'Mesa de Reunião Glass Premium', 3150.00);

-- =========================
-- PRODUCT_RAW_MATERIAL RELATIONS (Receitas de Produção)
-- ✅ CORRIGIDO: product_raw_material (não product_material)
-- ✅ CORRIGIDO: quantity_required (não required_quantity)
-- =========================

-- Escrivaninha Industrial (Aço + Madeira)
INSERT INTO product_raw_material (product_id, raw_material_id, quantity_required)
SELECT p.id, r.id, 12.000
FROM product p, raw_material r
WHERE p.code = 'PR-OFF-DESK' AND r.code = 'RM-STEEL';

INSERT INTO product_raw_material (product_id, raw_material_id, quantity_required)
SELECT p.id, r.id, 8.000
FROM product p, raw_material r
WHERE p.code = 'PR-OFF-DESK' AND r.code = 'RM-OAK';

-- Sofá Minimalista (Madeira + Couro + Espuma) - PRODUTO DE MAIOR VALOR
INSERT INTO product_raw_material (product_id, raw_material_id, quantity_required)
SELECT p.id, r.id, 10.000
FROM product p, raw_material r
WHERE p.code = 'PR-LUX-SOFA' AND r.code = 'RM-OAK';

INSERT INTO product_raw_material (product_id, raw_material_id, quantity_required)
SELECT p.id, r.id, 15.000
FROM product p, raw_material r
WHERE p.code = 'PR-LUX-SOFA' AND r.code = 'RM-LEATH';

INSERT INTO product_raw_material (product_id, raw_material_id, quantity_required)
SELECT p.id, r.id, 20.000
FROM product p, raw_material r
WHERE p.code = 'PR-LUX-SOFA' AND r.code = 'RM-FOAM';

-- Armário Corta-Fogo (Aço + Resina)
INSERT INTO product_raw_material (product_id, raw_material_id, quantity_required)
SELECT p.id, r.id, 35.000
FROM product p, raw_material r
WHERE p.code = 'PR-MTL-CAB' AND r.code = 'RM-STEEL';

INSERT INTO product_raw_material (product_id, raw_material_id, quantity_required)
SELECT p.id, r.id, 5.000
FROM product p, raw_material r
WHERE p.code = 'PR-MTL-CAB' AND r.code = 'RM-RESIN';

-- Mesa de Reunião Glass (Vidro + Alumínio)
INSERT INTO product_raw_material (product_id, raw_material_id, quantity_required)
SELECT p.id, r.id, 10.000
FROM product p, raw_material r
WHERE p.code = 'PR-GLS-TAB' AND r.code = 'RM-GLASS';

INSERT INTO product_raw_material (product_id, raw_material_id, quantity_required)
SELECT p.id, r.id, 15.000
FROM product p, raw_material r
WHERE p.code = 'PR-GLS-TAB' AND r.code = 'RM-ALUM';

COMMIT;

-- =========================
-- CÁLCULO ESPERADO DE PRODUÇÃO
-- =========================
-- Com os estoques atuais:
--
-- Escrivaninha Industrial (PR-OFF-DESK) - Preço: R$ 1.850,00
--   - Aço: 500 / 12 = 41 unidades
--   - Madeira: 300 / 8 = 37 unidades
--   MAX: 37 unidades (limitado por madeira)
--   VALOR: 37 × 1.850 = R$ 68.450,00
--
-- Sofá Minimalista (PR-LUX-SOFA) - Preço: R$ 4.200,00 ⭐ MAIOR VALOR
--   - Madeira: 300 / 10 = 30 unidades
--   - Couro: 150 / 15 = 10 unidades  ⚠️ GARGALO
--   - Espuma: 320 / 20 = 16 unidades
--   MAX: 10 unidades (limitado por couro)
--   VALOR: 10 × 4.200 = R$ 42.000,00
--
-- Armário Corta-Fogo (PR-MTL-CAB) - Preço: R$ 2.100,00
--   - Aço: 500 / 35 = 14 unidades
--   - Resina: 270 / 5 = 54 unidades
--   MAX: 14 unidades (limitado por aço)
--   VALOR: 14 × 2.100 = R$ 29.400,00
--
-- Mesa de Reunião Glass (PR-GLS-TAB) - Preço: R$ 3.150,00
--   - Vidro: 200 / 10 = 20 unidades
--   - Alumínio: 400 / 15 = 26 unidades
--   MAX: 20 unidades (limitado por vidro)
--   VALOR: 20 × 3.150 = R$ 63.000,00
--
-- =========================
-- SUGESTÃO DE PRODUÇÃO (priorizar por VALOR - requisito do teste)
-- =========================
-- 1º) Escrivaninha: 37 unidades = R$ 68.450,00
-- 2º) Mesa Glass: 20 unidades = R$ 63.000,00
-- 3º) Sofá: 10 unidades = R$ 42.000,00
-- 4º) Armário: 14 unidades = R$ 29.400,00
--
-- VALOR TOTAL POSSÍVEL: R$ 202.850,00
-- =========================