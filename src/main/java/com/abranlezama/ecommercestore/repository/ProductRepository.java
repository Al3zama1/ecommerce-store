package com.abranlezama.ecommercestore.repository;

import com.abranlezama.ecommercestore.model.CategoryType;
import com.abranlezama.ecommercestore.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
    SELECT DISTINCT p FROM Product p JOIN p.productCategories c WHERE c.category
    IN :categories
    """)
    Page<Product> findProductByCategory(Pageable pageable, @Param("categories") Set<CategoryType> categories);
}
