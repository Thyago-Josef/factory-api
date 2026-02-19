package tech.josef.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductRawMaterialId implements Serializable {

    private Long product;      // Mesmo nome do campo na entidade
    private Long rawMaterial;  // Mesmo nome do campo na entidade
}