package tech.josef.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @SequenceGenerator(
            name = "productSeq",
            sequenceName = "product_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "productSeq"
    )
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;


    @Column(nullable = false, length = 200, unique = true)
    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    private BigDecimal price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductRawMaterial> rawMaterials = new ArrayList<>();

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

    // Helper method to add raw material
    public void addRawMaterial(RawMaterial rawMaterial, BigDecimal quantityRequired) {
        ProductRawMaterial productRawMaterial = new ProductRawMaterial();
        productRawMaterial.setProduct(this);
        productRawMaterial.setRawMaterial(rawMaterial);
        productRawMaterial.setQuantityRequired(quantityRequired);
        rawMaterials.add(productRawMaterial);
    }

    // Helper method to remove raw material
    public void removeRawMaterial(RawMaterial rawMaterial) {
        rawMaterials.removeIf(prm ->
                prm.getRawMaterial().getId().equals(rawMaterial.getId())
        );
    }
}