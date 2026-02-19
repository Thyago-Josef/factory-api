package tech.josef.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import tech.josef.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

    /**
     * Find product by unique code
     */
    public Optional<Product> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }

    /**
     * Find products with price greater than or equal to specified value
     */
    public List<Product> findByMinPrice(BigDecimal minPrice) {
        return find("price >= ?1", minPrice).list();
    }

    /**
     * Find products within price range
     */
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return find("price >= ?1 AND price <= ?2", minPrice, maxPrice).list();
    }

    /**
     * Search products by name (case-insensitive, partial match)
     */
    public List<Product> searchByName(String name) {
        return find("LOWER(name) LIKE LOWER(?1)", "%" + name + "%").list();
    }

    /**
     * Check if product code already exists
     */
    public boolean existsByCode(String code) {
        return count("code", code) > 0;
    }

    /**
     * Find all products with their raw materials (eager fetch to avoid N+1)
     */
    public List<Product> findAllWithRawMaterials() {
        return find("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.rawMaterials").list();
    }

    /**
     * Busca todos os produtos ordenados pelo preço decrescente.
     * Essencial para a regra de priorização de lucro (RF004).
     */
    public List<Product> listAllSortedByPriceDesc() {
        return listAll(Sort.by("price").descending());
    }

    public List<Product> findAllWithRawMaterialsSorted() {
        // O 'JOIN FETCH' é o segredo: ele obriga o banco a trazer os materiais junto com o produto
        return find("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.rawMaterials ORDER BY p.price DESC").list();
    }
}
