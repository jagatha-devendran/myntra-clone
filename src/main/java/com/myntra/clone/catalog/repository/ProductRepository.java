package com.myntra.clone.catalog.repository;
import com.myntra.clone.catalog.dto.SuggestionDto;
import com.myntra.clone.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT new com.myntra.clone.catalog.dto.SuggestionDto(p.category, COUNT(*))
        FROM Product p
        WHERE p.isActive = true AND LOWER(p.category) LIKE LOWER(CONCAT(:query, '%'))
        GROUP BY p.category
        ORDER BY COUNT(p.id) DESC
    """)
    List<SuggestionDto> findCategorySuggestions(@Param("query") String query, Pageable pageable);

    @Query("""
        SELECT new com.myntra.clone.catalog.dto.SuggestionDto(b.name, COUNT(*))
        FROM Product p
        JOIN p.brand b
        WHERE p.isActive = true AND LOWER(b.name) LIKE LOWER(CONCAT(:query, '%'))
        GROUP BY b.name
        ORDER BY COUNT(p.id) DESC
    """)
    List<SuggestionDto> findBrandSuggestions(@Param("query") String query, Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        JOIN FETCH p.brand
        WHERE p.isActive = true AND LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%'))
""")
    Page<Product> findByCategory(@Param("category") String query, Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        JOIN FETCH p.brand b
        WHERE p.isActive = true AND LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%'))
        AND LOWER(b.name) = LOWER(:brandName)
""")
    Page<Product> findByCategoryAndBrand(@Param("category") String query, @Param("brandName") String brandName, Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        JOIN FETCH p.brand
        WHERE (p.id = :id OR p.myntraProductId = :id)
        AND p.isActive = true
""")
    Product findActiveProductWithBrandById(@Param("id") Long id);

    Optional<Product> findByMyntraProductIdOrId(Long pid, Long id);

}
