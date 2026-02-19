package tech.josef.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class ProductDTO {
    private Long id;
    private String code;
    private String name;
    private BigDecimal price;

    // Lista de materiais necessários para fabricar este produto
    private List<ProductMaterialRequestDTO> materials;

    @Getter @Setter
    public static class ProductMaterialRequestDTO {
        private Long rawMaterialId;
        private BigDecimal quantityRequired;
    }
}