package tech.josef.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "product_raw_material")
@IdClass(ProductRawMaterialId.class)
@Getter
@Setter
@NoArgsConstructor
public class ProductRawMaterial {

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    @Column(name = "quantity_required", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantityRequired;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductRawMaterial)) return false;
        ProductRawMaterial that = (ProductRawMaterial) o;
        return Objects.equals(product.getId(), that.product.getId()) &&
                Objects.equals(rawMaterial.getId(), that.rawMaterial.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(product.getId(), rawMaterial.getId());
    }
}