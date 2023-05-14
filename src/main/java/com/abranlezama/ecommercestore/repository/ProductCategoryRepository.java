package com.abranlezama.ecommercestore.repository;

import com.abranlezama.ecommercestore.model.ProductCategory;
import com.abranlezama.ecommercestore.model.ProductCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategoryId> {
}
