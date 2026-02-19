package tech.josef.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import tech.josef.entity.Product;
import tech.josef.entity.ProductRawMaterial;
import tech.josef.entity.ProductRawMaterialId;
import tech.josef.entity.RawMaterial;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRawMaterialRepository
        implements PanacheRepositoryBase<ProductRawMaterial, ProductRawMaterialId> {

    /**
     * Find all raw materials for a specific product
     */
    public List<ProductRawMaterial> findByProduct(Product product) {
        return find("product", product).list();
    }

    /**
     * Find all raw materials for a product by product ID
     */
    public List<ProductRawMaterial> findByProductId(Long productId) {
        return find("product.id", productId).list();
    }

    /**
     * Find all products that use a specific raw material
     */
    public List<ProductRawMaterial> findByRawMaterial(RawMaterial rawMaterial) {
        return find("rawMaterial", rawMaterial).list();
    }

    /**
     * Find all products that use a raw material by raw material ID
     */
    public List<ProductRawMaterial> findByRawMaterialId(Long rawMaterialId) {
        return find("rawMaterial.id", rawMaterialId).list();
    }

    /**
     * Find specific association between product and raw material
     */
    public Optional<ProductRawMaterial> findByProductAndRawMaterial(
            Product product, RawMaterial rawMaterial) {
        return find("product = ?1 AND rawMaterial = ?2", product, rawMaterial)
                .firstResultOptional();
    }

    /**
     * Check if association exists between product and raw material
     */
    public boolean exists(Product product, RawMaterial rawMaterial) {
        return count("product = ?1 AND rawMaterial = ?2", product, rawMaterial) > 0;
    }

    /**
     * Delete all associations for a product
     */
    public long deleteByProduct(Product product) {
        return delete("product", product);
    }

    /**
     * Delete all associations for a raw material
     */
    public long deleteByRawMaterial(RawMaterial rawMaterial) {
        return delete("rawMaterial", rawMaterial);
    }

    /**
     * Count how many products use a specific raw material
     */
    public long countProductsUsingRawMaterial(RawMaterial rawMaterial) {
        return count("rawMaterial", rawMaterial);
    }
}