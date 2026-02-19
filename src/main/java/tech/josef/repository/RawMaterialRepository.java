package tech.josef.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import tech.josef.entity.RawMaterial;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RawMaterialRepository implements PanacheRepository<RawMaterial> {

    /**
     * Find raw material by unique code
     */
    public Optional<RawMaterial> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }

    /**
     * Find raw materials with stock below minimum threshold
     */
    public List<RawMaterial> findLowStock(BigDecimal minQuantity) {
        return find("stockQuantity < ?1", minQuantity).list();
    }

    /**
     * Find raw materials with stock above specified quantity
     */
    public List<RawMaterial> findAvailableStock(BigDecimal minQuantity) {
        return find("stockQuantity >= ?1", minQuantity).list();
    }

    /**
     * Find raw materials with zero stock
     */
    public List<RawMaterial> findOutOfStock() {
        return find("stockQuantity = 0").list();
    }

    /**
     * Search raw materials by name (case-insensitive, partial match)
     */
    public List<RawMaterial> searchByName(String name) {
        return find("LOWER(name) LIKE LOWER(?1)", "%" + name + "%").list();
    }

    /**
     * Check if raw material code already exists
     */
    public boolean existsByCode(String code) {
        return count("code", code) > 0;
    }

    /**
     * Get total count of raw materials in stock
     */
    public long countInStock() {
        return count("stockQuantity > 0");
    }
}