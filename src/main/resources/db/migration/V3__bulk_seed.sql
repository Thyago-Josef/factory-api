-- V3__insert_bulk_test_data.sql
-- CORRIGIDO: Gera dados em massa para teste de performance

DECLARE
-- Tipos para simular nomes reais
TYPE t_name IS TABLE OF VARCHAR2(50);
    v_categories t_name := t_name('Armário', 'Cadeira', 'Suporte', 'Painel', 'Prateleira');
    v_types      t_name := t_name('Industrial', 'Premium', 'Standard', 'Eco', 'Minimalista');
    v_materials  t_name := t_name('Aço', 'Carvalho', 'Alumínio', 'Polímero', 'Vidro');
    
    v_prod_name VARCHAR2(100);
    v_mat_id    NUMBER;
    v_prod_id   NUMBER;
BEGIN
    -- =====================================================
    -- 1. Gerar 100 Matérias-Primas
    -- =====================================================
FOR i IN 1..100 LOOP
        INSERT INTO raw_material (
            id,                              -- ✅ CORRIGIDO: Adicionar id
            code, 
            name, 
            stock_quantity
        ) VALUES (
            raw_material_seq.NEXTVAL,        -- ✅ CORRIGIDO: Usar sequência
            'RM' || LPAD(i, 3, '0'),         -- ✅ MELHOR: RM001, RM002, etc
            'Insumo ' || v_materials(MOD(i - 1, 5) + 1) || ' Tipo ' || i,
            ROUND(DBMS_RANDOM.VALUE(100, 500), 3)  -- ✅ 3 decimais
        );
END LOOP;

    -- =====================================================
    -- 2. Gerar 50 Produtos com Nomes Combinados
    -- =====================================================
FOR i IN 1..50 LOOP
        v_prod_name := v_categories(MOD(i - 1, 5) + 1) || ' ' || 
                       v_types(MOD(i - 1, 3) + 1) || ' ' || 
                       v_types(MOD(i - 1, 5) + 1);

INSERT INTO product (
    id,                              -- ✅ CORRIGIDO: Adicionar id
    code,
    name,
    price
) VALUES (
             product_seq.NEXTVAL,             -- ✅ CORRIGIDO: Usar sequência
             'PR' || LPAD(i, 3, '0'),         -- ✅ MELHOR: PR001, PR002, etc
             v_prod_name || ' #' || i,
             ROUND(DBMS_RANDOM.VALUE(200, 5000), 2)
         );
END LOOP;

    -- =====================================================
    -- 3. Vincular 3 matérias-primas aleatórias para cada produto
    -- ✅ CORRIGIDO: product_raw_material e quantity_required
    -- =====================================================
FOR p IN (SELECT id FROM product WHERE code LIKE 'PR%') LOOP
        FOR i IN 1..3 LOOP
            -- Pega um material aleatório que ainda não foi associado
BEGIN
SELECT id INTO v_mat_id
FROM (
         SELECT rm.id
         FROM raw_material rm
         WHERE NOT EXISTS (
             SELECT 1
             FROM product_raw_material prm  -- ✅ CORRIGIDO: nome da tabela
             WHERE prm.product_id = p.id
               AND prm.raw_material_id = rm.id
         )
         ORDER BY DBMS_RANDOM.VALUE
     )
WHERE ROWNUM = 1;

-- Insere associação
INSERT INTO product_raw_material (  -- ✅ CORRIGIDO: nome da tabela
    product_id,
    raw_material_id,
    quantity_required                -- ✅ CORRIGIDO: nome da coluna
) VALUES (
             p.id,
             v_mat_id,
             ROUND(DBMS_RANDOM.VALUE(1, 15), 3)  -- ✅ 3 decimais
         );

EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    NULL; -- Se não houver material disponível, pula
END;
END LOOP;
END LOOP;

COMMIT;

-- Mensagem de sucesso
DBMS_OUTPUT.PUT_LINE('✅ Dados em massa gerados com sucesso!');
    DBMS_OUTPUT.PUT_LINE('   - 100 Matérias-primas adicionadas');
    DBMS_OUTPUT.PUT_LINE('   - 50 Produtos adicionados');
    DBMS_OUTPUT.PUT_LINE('   - ~150 Associações criadas');

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('❌ Erro ao gerar dados: ' || SQLERRM);
        RAISE;
END;
/

-- =====================================================
-- Verificação dos dados inseridos
-- =====================================================
SELECT 'Total Raw Materials: ' || COUNT(*) AS info FROM raw_material
UNION ALL
SELECT 'Total Products: ' || COUNT(*) FROM product
UNION ALL
SELECT 'Total Associations: ' || COUNT(*) FROM product_raw_material;