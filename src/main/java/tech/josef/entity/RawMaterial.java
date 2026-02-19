package tech.josef.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "raw_material")
@Getter
@Setter
@NoArgsConstructor
public class RawMaterial {

    @Id
    @SequenceGenerator(
            name = "rawMaterialSeq",
            sequenceName = "raw_material_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "rawMaterialSeq"
    )
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "stock_quantity", nullable = false, precision = 15, scale = 3)
    @NotNull(message = "A quantidade é obrigatória")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal stockQuantity = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business logic methods
    public boolean hasEnoughStock(BigDecimal required) {
        return stockQuantity.compareTo(required) >= 0;
    }

    public void deductStock(BigDecimal quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalStateException(
                    String.format("Insufficient stock for %s. Available: %s, Required: %s",
                            name, stockQuantity, quantity)
            );
        }
        stockQuantity = stockQuantity.subtract(quantity);
    }

    public void addStock(BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        stockQuantity = stockQuantity.add(quantity);
    }
}
