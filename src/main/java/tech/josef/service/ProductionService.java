package tech.josef.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import tech.josef.dto.ProductionSuggestionDTO;
import tech.josef.entity.Product;
import tech.josef.entity.ProductRawMaterial;
import tech.josef.entity.RawMaterial;
import tech.josef.repository.ProductRepository;
import tech.josef.repository.RawMaterialRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductionService {

    @Inject
    ProductRepository productRepository;

    @Inject
    RawMaterialRepository rawMaterialRepository;

    /**
     * RF004: Sugere a produção otimizada baseada no maior preço unitário
     * e disponibilidade real de estoque, considerando competição por materiais.
     *
     * Usa estoque virtual para simular consumo sequencial sem modificar o banco.
     */
    public List<ProductionSuggestionDTO> suggestOptimizedProduction() {
        // 1. Busca produtos ordenados por preço (mais caro primeiro)
        List<Product> products = productRepository.listAllSortedByPriceDesc();
//    public List<ProductionSuggestionDTO> suggestOptimizedProduction() {
//        // 1. Busca produtos COM os materiais carregados e ordenados
//        List<Product> products = productRepository.findAllWithRawMaterialsSorted();

        // 2. Cria estoque virtual (cópia em memória do estoque real)
        Map<Long, BigDecimal> virtualStock = createVirtualStockMap();

        // 3. Calcula sugestões considerando consumo sequencial
        List<ProductionSuggestionDTO> suggestions = new ArrayList<>();

        for (Product product : products) {
            // Calcula com o estoque virtual atual
            ProductionSuggestionDTO suggestion = calculateMaxProduction(product, virtualStock);

            if (suggestion.getSuggestedQuantity() > 0) {
                suggestions.add(suggestion);

                // Deduz do estoque virtual para o próximo produto
                deductFromVirtualStock(product, suggestion.getSuggestedQuantity(), virtualStock);
            }
        }

        return suggestions;
    }

    /**
     * Calcula a quantidade máxima que pode ser produzida com o estoque disponível.
     * Retorna o menor valor entre todos os materiais (gargalo).
     */
    private ProductionSuggestionDTO calculateMaxProduction(Product product, Map<Long, BigDecimal> virtualStock) {
        BigDecimal maxQuantity = null;

        for (ProductRawMaterial prm : product.getRawMaterials()) {
            // Usa estoque VIRTUAL (não o real do banco)
            BigDecimal available = virtualStock.get(prm.getRawMaterial().getId());
            BigDecimal required = prm.getQuantityRequired();

            // Calcula quantas unidades esse material permite fazer
            BigDecimal possibleForThisMaterial = available.divide(required, 0, RoundingMode.DOWN);

            // Guarda o menor (gargalo/bottleneck)
            if (maxQuantity == null || possibleForThisMaterial.compareTo(maxQuantity) < 0) {
                maxQuantity = possibleForThisMaterial;
            }
        }

        if (maxQuantity == null) maxQuantity = BigDecimal.ZERO;

        return new ProductionSuggestionDTO(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getPrice(),
                maxQuantity.intValue(),
                product.getPrice().multiply(maxQuantity)
        );
    }

    /**
     * Cria um mapa com o estoque atual de todas as matérias-primas.
     * Este é o "estoque virtual" que será usado para simulação.
     */
    private Map<Long, BigDecimal> createVirtualStockMap() {
        Map<Long, BigDecimal> virtualStock = new HashMap<>();
        rawMaterialRepository.listAll().forEach(rm ->
                virtualStock.put(rm.getId(), rm.getStockQuantity())
        );
        return virtualStock;
    }

    /**
     * Deduz as matérias-primas consumidas do estoque virtual.
     * NÃO modifica o banco de dados, apenas a simulação em memória.
     */
    private void deductFromVirtualStock(Product product, Integer quantity, Map<Long, BigDecimal> virtualStock) {
        for (ProductRawMaterial prm : product.getRawMaterials()) {
            Long materialId = prm.getRawMaterial().getId();
            BigDecimal consumed = prm.getQuantityRequired().multiply(BigDecimal.valueOf(quantity));
            BigDecimal remaining = virtualStock.get(materialId).subtract(consumed);
            virtualStock.put(materialId, remaining);
        }
    }

    /**
     * Executa a produção de fato, abatendo o estoque REAL do banco.
     * Apenas este método modifica o banco de dados.
     */
    @Transactional // ✅ CRUCIAL: Sem isso o Hibernate não salva no Oracle
    public void executeProduction(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new NotFoundException("Produto não encontrado");
        }

        // Valida estoque ANTES de deduzir
        for (ProductRawMaterial prm : product.getRawMaterials()) {
            RawMaterial rm = prm.getRawMaterial();
            BigDecimal totalRequired = prm.getQuantityRequired()
                    .multiply(BigDecimal.valueOf(quantity));

            if (!rm.hasEnoughStock(totalRequired)) {
                throw new IllegalStateException(
                        String.format("Estoque insuficiente de %s. Necessário: %s, Disponível: %s",
                                rm.getName(), totalRequired, rm.getStockQuantity())
                );
            }
        }

        // Deduz do estoque REAL
        for (ProductRawMaterial prm : product.getRawMaterials()) {
            RawMaterial rm = prm.getRawMaterial();
            BigDecimal totalToDeduct = prm.getQuantityRequired()
                    .multiply(BigDecimal.valueOf(quantity));

            rm.deductStock(totalToDeduct);
            // Hibernate detecta a mudança e faz UPDATE automaticamente
        }
    }


}
