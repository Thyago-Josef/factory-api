package tech.josef.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class RawMaterialDTO {
    private Long id;
    private String code;
    private String name;
    private BigDecimal stockQuantity;
}