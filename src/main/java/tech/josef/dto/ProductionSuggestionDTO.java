package tech.josef.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionSuggestionDTO {

    private Long productId;

    private String productCode;  // ✅ ADICIONAR (útil para identificação)

    private String productName;

    private BigDecimal price;  // Preço unitário

    private Integer suggestedQuantity;  // ✅ RENOMEAR: mais claro que é uma sugestão

    private BigDecimal totalValue;  // ✅ RENOMEAR: price × suggestedQuantity
}

