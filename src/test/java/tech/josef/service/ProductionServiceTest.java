package tech.josef.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.josef.entity.Product;
import tech.josef.entity.RawMaterial;
import tech.josef.repository.ProductRepository;
import tech.josef.repository.RawMaterialRepository;

import java.math.BigDecimal;
import java.util.List;

@QuarkusTest
public class ProductionServiceTest {

    @Inject
    ProductionService service; // Injeta o serviço real

    @InjectMock
    ProductRepository repository; // Simula o banco de dados

    @InjectMock
    RawMaterialRepository rawMaterialRepository;

    @Test
    void naoDeveProduzirSeFaltarEstoque() {
        // ARRANGE (Cenário)
        Product p = new Product();
        p.setId(1L);
        RawMaterial rm = new RawMaterial();
        rm.setStockQuantity(new BigDecimal("5.0")); // Só tem 5 no estoque
        p.addRawMaterial(rm, new BigDecimal("10.0")); // Mas precisa de 10

        Mockito.when(repository.findById(1L)).thenReturn(p);

        // ACT & ASSERT (Ação e Verificação)
        Assertions.assertThrows(IllegalStateException.class, () -> {
            service.executeProduction(1L, 1);
        });
    }


    @Test
    @DisplayName("Deve subtrair o estoque real corretamente após a execução")
    void deveSubtrairEstoqueReal() {
        // ARRANGE
        Product p = new Product();
        p.setId(1L);
        RawMaterial rm = new RawMaterial();
        rm.setStockQuantity(new BigDecimal("10.0")); // Começa com 10
        p.addRawMaterial(rm, new BigDecimal("2.0")); // Gasta 2 por unidade

        Mockito.when(repository.findById(1L)).thenReturn(p);

        // ACT
        service.executeProduction(1L, 3); // Produz 3 (deve gastar 6)

        // ASSERT
        Assertions.assertEquals(new BigDecimal("4.0"), rm.getStockQuantity());
    }

    @Test
    @DisplayName("A sugestão deve ser limitada pelo material com menor estoque (gargalo)")
    void sugestaoDeveRespeitarGargalo() {
        // ARRANGE
        Product p = new Product();
        p.setName("Mesa");
        p.setPrice(new BigDecimal("100.0"));

        RawMaterial madeira = new RawMaterial();
        madeira.setId(1L);
        madeira.setStockQuantity(new BigDecimal("100.0")); // Tem muita madeira

        RawMaterial cola = new RawMaterial();
        cola.setId(2L);
        cola.setStockQuantity(new BigDecimal("2.0")); // Só tem 2 unidades de cola

        p.addRawMaterial(madeira, new BigDecimal("10.0"));
        p.addRawMaterial(cola, new BigDecimal("1.0")); // Cada mesa usa 1 cola

        Mockito.when(repository.listAllSortedByPriceDesc()).thenReturn(List.of(p));
        Mockito.when(rawMaterialRepository.listAll()).thenReturn(List.of(madeira, cola));

        // ACT
        var sugestoes = service.suggestOptimizedProduction();

        // ASSERT
        // Embora tenha madeira para 10 mesas, a cola só permite 2 mesas.
        Assertions.assertEquals(2, sugestoes.get(0).getSuggestedQuantity());
    }


    @Test
    @DisplayName("Produto mais caro deve consumir estoque e limitar o próximo produto")
    void produtoCaroDeveConsumirEstoquePrimeiro() {
        // ARRANGE
        RawMaterial aco = new RawMaterial();
        aco.setId(1L);
        aco.setStockQuantity(new BigDecimal("10.0"));

        Product caro = new Product();
        caro.setPrice(new BigDecimal("500.0"));
        caro.addRawMaterial(aco, new BigDecimal("10.0")); // Consome tudo

        Product barato = new Product();
        barato.setPrice(new BigDecimal("100.0"));
        barato.addRawMaterial(aco, new BigDecimal("1.0"));

        Mockito.when(repository.listAllSortedByPriceDesc()).thenReturn(List.of(caro, barato));
        Mockito.when(rawMaterialRepository.listAll()).thenReturn(List.of(aco));

        // ACT
        var sugestoes = service.suggestOptimizedProduction();

        // ASSERT
        Assertions.assertEquals(1, sugestoes.size());
        Assertions.assertEquals(new BigDecimal("500.0"), sugestoes.get(0).getPrice());
        // O produto barato não deve aparecer na lista pois o estoque virtual zerou
    }
}
